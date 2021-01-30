import {Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {NotificationService} from 'src/app/services/notification.service';
import {Category} from '../../dtos/category';
import {CategoryService} from '../../services/category.service';
import {MLocSpot} from '../../util/m-loc-spot';


@Component({
  selector: 'app-spot-form [title] [lastLayer]',
  templateUrl: './spot-form.component.html',
  styleUrls: ['./spot-form.component.scss']
})
export class SpotFormComponent implements OnInit, OnChanges, OnDestroy {

  @Input() spot: MLocSpot = null;
  @Input() title: string = '';
  @Input() lastLayer: boolean = true;

  @Output() cancel = new EventEmitter();
  @Output() confirm = new EventEmitter<MLocSpot>();

  spotForm: FormGroup;
  categories: Category[] = [];

  private subscription: Subscription;

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

    this.subscription = this.categoryService.getAll().subscribe(
      result => {
        this.categories = result;
      }, error => {
        this.notificationService.error('Error loading categories!');
        console.log(error);
      }
    );
  }

  ngOnDestroy(): void {
    if (this.subscription != null) {
      this.subscription.unsubscribe();
    }
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
    const newSpot = new MLocSpot(null, val.name, val.description, val.category, null, null);

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
