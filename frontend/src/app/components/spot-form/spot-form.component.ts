import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SpotService} from '../../services/spot.service';
import {Category} from '../../dtos/category';
import {CategoryService} from '../../services/category.service';
import {MLocSpot} from '../../util/m-loc-spot';
import {MLocation} from '../../util/m-location';
import { Marker } from 'leaflet';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-spot-form',
  templateUrl: './spot-form.component.html',
  styleUrls: ['./spot-form.component.scss']
})
export class SpotFormComponent implements OnInit {

  // for existing locatoins
  @Input() locationId: number = null;

  // for new locatoins:
  @Input() marker: Marker = null;

  @Output() savedSpot = new EventEmitter<MLocSpot>();
  @Output() cancel = new EventEmitter();

  spotForm: FormGroup;
  categories: Category[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    private spotService: SpotService,
    private activedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.spotForm = this.formBuilder.group({
      name: [null, [Validators.required, Validators.minLength(1)]],
      description: [null, [Validators.required]],
      category: [null, [Validators.required]]
    });

    this.categoryService.getAllCategories().subscribe(result => {
      this.categories = result;
    });
  }

  saveSpot(): void {
    console.log('in saveSpot(): ');

    let loc: MLocation;

    if (this.marker != null) {
      loc = new MLocation(null, this.marker.getLatLng().lat, this.marker.getLatLng().lng);
    } else {
      console.log('no marker');
      console.log(this.locationId);
      loc = new MLocation(this.locationId, 0.0, 0.0);
    }

    const newSpot = new MLocSpot(null, this.spotForm.value.name, this.spotForm.value.description, this.spotForm.value.category, loc);

    this.spotService.createSpot(newSpot).subscribe(result => {
      this.savedSpot.emit(result);
    });
  }

  cancelCreateSpot(): void {
    this.cancel.emit();
  }
}
