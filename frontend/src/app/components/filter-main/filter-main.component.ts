import { Component, OnInit } from '@angular/core';
import {Category} from '../../dtos/category';
import {BehaviorSubject, Subscription} from 'rxjs';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {CategoryService} from '../../services/category.service';
import {NotificationService} from '../../services/notification.service';
import {Router, UrlSerializer} from '@angular/router';
import {MessageService} from '../../services/message.service';
import {LocationService} from '../../services/location.service';
import {Hashtag} from '../../dtos/hashtag';
import {HashtagService} from '../../services/hashtag.service';
import {SidebarService} from '../../services/sidebar.service';

@Component({
  selector: 'app-filter-main',
  templateUrl: './filter-main.component.html',
  styleUrls: ['./filter-main.component.scss']
})
export class FilterMainComponent implements OnInit {

  categories: Category[];
  hashtags: Hashtag[];
  radius: number = 0;
  strLoc: string;
  strMes: string;

  messageForm: FormGroup;
  locationForm: FormGroup;

  panelOpenState = false;

  sidebarActive: boolean = false;
  private subscription: Subscription;

  paramMessage = new BehaviorSubject<string>('categoryMes=&hashtag=&time=');
  paramLocation = new BehaviorSubject<string>('categoryLoc=&latitude=&longitude=&radius=');

  constructor(private formBuilder: FormBuilder,
              private categoryService: CategoryService,
              private messageService: MessageService,
              private hashtagService: HashtagService,
              private locationService: LocationService,
              private sidebarService: SidebarService,
              private notificationService: NotificationService,
              private serializer: UrlSerializer,
              private router: Router) {

    this.locationForm = this.formBuilder.group({
      categoryLoc: [''],
      latitude: [''],
      longitude: [''],
      radius: [''],
    });

    this.messageForm = this.formBuilder.group({
      categoryMes: [''],
      hashtag: [''],
      time: ['']
    });
  }

  ngOnInit(): void {
    this.getAllCategories();
    this.getAllHashtags();
    this.buildMessageForm();
    this.buildLocationForm();

    this.sidebarActive = !(this.router.routerState.snapshot.url.toString() === '/');
    if (this.sidebarActive) {
      this.sidebarService.setSidebarStateOpen();
    } else {
      this.sidebarService.setSidebarStateClosed();
    }

    this.subscription = this.sidebarService.changeVisibilityAndFocusObservable.subscribe(change => {
      this.sidebarActive = change.isVisible;
    });
  }

  updateSetting(event) {
    this.radius = event.value;
  }

  filterLoc(): void {
    const formData = {
      categoryLoc: this.locationForm.get('categoryLoc').value,
      latitude: this.locationForm.get('latitude').value,
      longitude: this.locationForm.get('longitude').value,
      radius: this.locationForm.get('radius').value,
    };
    this.strLoc = this.serializer.serialize(this.router.createUrlTree([], { queryParams: formData }));
    this.paramLocation.next(this.strLoc);
    this.locationService.filterLocation(this.strLoc);
  }

  filterMes(): void {
    const formData = {
      categoryMes: this.messageForm.get('categoryMes').value,
      hashtag: this.messageForm.get('hashtag').value,
      time: this.messageForm.get('time').value
    };
    this.strMes = this.serializer.serialize(this.router.createUrlTree([], { queryParams: formData }));
    this.paramMessage.next(this.strMes);

    this.messageService.filterMessage(this.strMes);

    this.sidebarService.changeVisibilityAndFocus({isVisible: true});
    this.sidebarActive = true;
  }

  buildLocationForm(): void {
    this.locationForm = this.formBuilder.group({
      categoryLoc: new FormControl(''),
      latitude: new FormControl(''),
      longitude: new FormControl(''),
      radius: new FormControl('')
    });
  }

  buildMessageForm(): void {
    this.messageForm = this.formBuilder.group({
      categoryMes: new FormControl(''),
      hashtag: new FormControl(''),
      time: new FormControl('')
    });
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

  getAllHashtags(): void {
    this.hashtagService.getAll().subscribe(
      result => {
        this.hashtags = result;
      }, error => {
        this.notificationService.error('Error loading hashtags!');
        console.log(error);
      }
    );
  }

  onSidebarActive(sidebarActive: boolean) {
    this.sidebarActive = sidebarActive;
  }

}
