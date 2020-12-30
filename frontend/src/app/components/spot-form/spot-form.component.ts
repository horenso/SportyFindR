import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SpotService} from '../../services/spot.service';
import {Category} from '../../dtos/category';
import {CategoryService} from '../../services/category.service';
import {MLocSpot} from '../../util/m-loc-spot';
import {Marker} from 'leaflet';
import {MLocation} from '../../util/m-location';

@Component({
  selector: 'app-spot-form',
  templateUrl: './spot-form.component.html',
  styleUrls: ['./spot-form.component.scss']
})
export class SpotFormComponent implements OnInit {

  @Input() spot: MLocSpot;
  @Input() marker: Marker;
  @Output() savedSpot = new EventEmitter<MLocSpot>();
  @Output() cancel = new EventEmitter();

  spotForm: FormGroup;
  categories: Category[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    private spotService: SpotService) {
  }

  ngOnInit(): void {
    if (this.spot === undefined) {
      this.spot = new MLocSpot(null, null, null, null, null);
    }

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
    const newMLoc: MLocation = new MLocation(null, this.marker.getLatLng().lat, this.marker.getLatLng().lng);
    const newSpot: MLocSpot = new MLocSpot(null, this.spotForm.value.name, this.spotForm.value.description, this.spotForm.value.category, newMLoc);
    console.log(JSON.stringify(newSpot));
    this.spotService.createSpot(newSpot).subscribe(result => {
      this.savedSpot.emit(result);
    });
  }

  cancelCreateSpot(): void {
    this.cancel.emit();
  }
}
