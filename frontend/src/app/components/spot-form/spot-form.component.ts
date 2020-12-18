import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Spot} from '../../dtos/spot';
import {SpotService} from '../../services/spot.service';
import {Location} from '../../dtos/location';
import {Category} from '../../dtos/category';
import {CategoryService} from '../../services/category.service';
import {MarkerLocation} from '../../util/marker-location';

@Component({
  selector: 'app-spot-form',
  templateUrl: './spot-form.component.html',
  styleUrls: ['./spot-form.component.scss']
})
export class SpotFormComponent implements OnInit {

  @Input() spot: Spot;
  @Input() location: Location;
  @Input() locationMarker: MarkerLocation;
  @Output() savedSpot = new EventEmitter<Spot>();
  @Output() cancel = new EventEmitter();

  spotForm: FormGroup;
  categories: Category[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    private spotService: SpotService) {
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
    const values = this.spotForm.value;
    if (this.locationMarker !== null && this.locationMarker !== undefined) {
      const latLng = this.locationMarker.getLatLng();
      this.location = new Location(null, latLng.lat, latLng.lng);
    }
    const newSpot = new Spot(null, values.name, values.description, values.category, this.location);
    console.log(JSON.stringify(newSpot));
    console.log(this.location);
    this.spotService.createSpot(newSpot).subscribe(result => {
      this.savedSpot.emit(result);
    });
  }
}
