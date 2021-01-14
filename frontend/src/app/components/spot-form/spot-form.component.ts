import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { NotificationService } from 'src/app/services/notification.service';
import {Category} from '../../dtos/category';
import {CategoryService} from '../../services/category.service';
import {MLocSpot} from '../../util/m-loc-spot';


@Component({
  selector: 'app-spot-form',
  templateUrl: './spot-form.component.html',
  styleUrls: ['./spot-form.component.scss']
})
export class SpotFormComponent implements OnInit, OnChanges {

  @Input() spot: MLocSpot = null;

  @Output() cancel = new EventEmitter();
  @Output() confirm = new EventEmitter<MLocSpot>();

  spotForm: FormGroup;
  categories: Category[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    private notificationService: NotificationService) {
  }

  ngOnChanges(): void {
    if (this.spotForm != null && this.spot != null) {
      this.setValues();
    }
  }

  ngOnInit(): void {
    this.spotForm = this.formBuilder.group({
      name: [null, [Validators.required, Validators.minLength(1)]],
      description: [null, [Validators.required]],
      category: [null, [Validators.required]]
    });

    if (this.spot != null) {
      this.setValues();
    }

    this.categoryService.getAll().subscribe(
      result => {
        this.categories = result;
      }, error => {
        this.notificationService.error('Error loading categories!');
        console.log(error);
      }
    );
  }

  private setValues(): void {
    this.spotForm.controls.name.setValue(this.spot.name);
    this.spotForm.controls.description.setValue(this.spot.description);
    this.spotForm.controls.category.setValue(this.spot.category);
    console.log('c same: ' + (this.spotForm.value.category === this.spot.category));
  }

  public compareCategory(a: Category, b: Category): boolean {
    return a?.id === b?.id;
  }

  onConfirm(): void {
    const val = this.spotForm.value;
    const newSpot = new MLocSpot(null, val.name, val.description, val.category, null);
    
    if (this.spot != null) {
      newSpot.id = this.spot.id;
      newSpot.markerLocation = this.spot.markerLocation;
    }

    this.confirm.emit(newSpot);
  }

  onCancel(): void {
    this.cancel.emit();
  }
}
