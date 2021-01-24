import {Component, OnDestroy, OnInit} from '@angular/core';
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
import {MapService} from '../../services/map.service';

@Component({
  selector: 'app-filter-main',
  templateUrl: './filter-main.component.html',
  styleUrls: ['./filter-main.component.scss']
})
export class FilterMainComponent implements OnInit, OnDestroy {

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

  constructor(private formBuilder: FormBuilder,
              private categoryService: CategoryService,
              private messageService: MessageService,
              private hashtagService: HashtagService,
              private locationService: LocationService,
              private sidebarService: SidebarService,
              private mapService: MapService,
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

    this.subscription = this.sidebarService.changeVisibilityAndFocusObservable.subscribe(change => {
      this.sidebarActive = change.isVisible;
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  updateSetting(event) {
    this.radius = event.value;
  }

  filterLoc(): void {
    this.mapService.updateFilter({
      categoryLoc: this.locationForm.get('categoryLoc').value,
      latitude: null,
      longitude: null,
      radius: this.locationForm.get('radius').value
    });
  }

  filterMes(): void {
    this.messageService.filterMessage({
      categoryMes: this.messageForm.get('categoryMes').value,
      hashtag: this.messageForm.get('hashtag').value,
      time: this.messageForm.get('time').value
    }).subscribe(result => {
      console.log("lala");
    });

    this.sidebarService.changeVisibilityAndFocus({isVisible: true});
    this.sidebarActive = true;
    this.router.navigate(['filter/messages']);
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
