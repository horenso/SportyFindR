import { Component, OnInit } from '@angular/core';
import {Category} from '../../dtos/category';
import {BehaviorSubject, Observable} from 'rxjs';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {CategoryService} from '../../services/category.service';
import {NotificationService} from '../../services/notification.service';
import {Router, UrlSerializer} from '@angular/router';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';
import {MessageService} from '../../services/message.service';
import {LocationService} from '../../services/location.service';
import {Location} from '../../dtos/location';
import {Message} from '../../dtos/message';

@Component({
  selector: 'app-filter-main',
  templateUrl: './filter-main.component.html',
  styleUrls: ['./filter-main.component.scss']
})
export class FilterMainComponent implements OnInit {

  messageSearch: Observable<Message[]>;
  locationSearch: Observable<Location[]>;

  categories: Category[];
  radius: number = 100000;

  messageForm: FormGroup;
  locationForm: FormGroup;

  paramMessage = new BehaviorSubject<string>('/messages/filter?category=&time=');
  paramLocation = new BehaviorSubject<string>('/locations/filter?category=&latitude=&longitude=&radius=');

  constructor(private formBuilder: FormBuilder,
              private categoryService: CategoryService,
              private messageService: MessageService,
              private locationService: LocationService,
              private notificationService: NotificationService,
              private serializer: UrlSerializer,
              private router: Router) {

    this.locationForm = this.formBuilder.group({
      category: [''],
      latitude: [''],
      longitude: [''],
      radius: [''],
    });

    this.messageForm = this.formBuilder.group({
      category: [''],
      time: [''],
    });
  }

  ngOnInit(): void {
    this.getAllCategories();
    this.buildMessageForm();
    this.buildLocationForm();

    this.messageSearch = this.paramMessage.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((str: string) => this.messageService.filterMessage(str))
    );

    this.locationSearch = this.paramLocation.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((str: string) => this.locationService.filterLocation(str))
    );
  }

  updateSetting(event) {
    this.radius = event.value;
  }

  searchLocation(str: string): void {
    const formData = {
      category: this.locationForm.get('categoryLoc').value,
      latitude: this.locationForm.get('latitude').value,
      longitude: this.locationForm.get('longitude').value,
      radius: this.radius,
    };
    console.log('MAH FIRST URL: ' + str);
    str = this.serializer.serialize(this.router.createUrlTree([], { queryParams: formData }));
    console.log('MAH URL: ' + str);
    this.paramLocation.next(str);
    console.log('MAH PARAM URL: ' + this.paramLocation.getValue());
  }

  searchMessage(str: string): void {
    const formData = {
      category: this.messageForm.get('categoryMes').value,
      time: this.messageForm.get('time').value,
    };
    console.log('MAH FIRST URL: ' + str);
    str = this.serializer.serialize(this.router.createUrlTree([], { queryParams: formData }));
    console.log('MAH URL: ' + str);
    this.paramLocation.next(str);
    console.log('MAH PARAM URL: ' + this.paramLocation.getValue());
  }

  buildLocationForm(): void {
    this.locationForm = this.formBuilder.group({
      category: new FormControl(''),
      latitude: new FormControl(''),
      longitude: new FormControl(''),
      radius: new FormControl('')
    });
  }

  buildMessageForm(): void {
    this.messageForm = this.formBuilder.group({
      category: new FormControl(''),
      time: new FormControl('')
    });
  }

  public compareCategory(a: Category, b: Category): boolean {
    return a?.id === b?.id;
  }


  getAllCategories(): void {
    this.categoryService.getAll().subscribe(
      result => {
        this.categories = result;
      }, error => {
        this.notificationService.error('Error loading categories!');
        console.log(error);
      }
    );
  }

  formatLabel(value: number) {
      return Math.round(value) + 'km';
  }

}
