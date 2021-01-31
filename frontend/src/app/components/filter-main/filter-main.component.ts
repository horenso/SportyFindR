import {Component, OnDestroy, OnInit} from '@angular/core';
import {Category} from '../../dtos/category';
import { Observable, Subscription} from 'rxjs';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {CategoryService} from '../../services/category.service';
import {NotificationService} from '../../services/notification.service';
import {Router, UrlSerializer} from '@angular/router';
import {MessageService} from '../../services/message.service';
import {LocationService} from '../../services/location.service';
import {HashtagService} from '../../services/hashtag.service';
import {SidebarService} from '../../services/sidebar.service';
import {MapService} from '../../services/map.service';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';
import {SimpleHashtag} from '../../dtos/simpleHashtag';
import {SimpleUser} from '../../dtos/simpleUser';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-filter-main',
  templateUrl: './filter-main.component.html',
  styleUrls: ['./filter-main.component.scss']
})
export class FilterMainComponent implements OnInit, OnDestroy {

  categories: Category[];
  hashtags: SimpleHashtag[];
  users: SimpleUser[];
  radius: number = 0;
  strLoc: string;
  strMes: string;

  messageForm: FormGroup;
  locationForm: FormGroup;

  panelOpenState = false;

  disabled = true;

  filteredHashtagOptions: Observable<SimpleHashtag[]>;
  hashtagControl = new FormControl();
  hashtagSelection: string;

  filteredUserOptions: Observable<SimpleUser[]>;
  userControl = new FormControl();
  userSelection: string;

  sidebarActive: boolean = false;
  private subscription: Subscription;

  constructor(private formBuilder: FormBuilder,
              private categoryService: CategoryService,
              private messageService: MessageService,
              private hashtagService: HashtagService,
              private locationService: LocationService,
              private sidebarService: SidebarService,
              private mapService: MapService,
              private userService: UserService,
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
      user: [''],
      hashtag: [''],
      time: ['']
    });
  }

  ngOnInit(): void {
    this.getAllCategories();
    this.buildMessageForm();
    this.buildLocationForm();

    this.subscription = this.sidebarService.changeVisibilityAndFocusObservable.subscribe(change => {
      this.sidebarActive = change.isVisible;
    });

    // Hashtag Filter
    this.filteredHashtagOptions = this.hashtagControl.valueChanges
      .pipe(
        debounceTime(200),
        distinctUntilChanged(),
        switchMap((str: string) => this.hashtagService.search(str))
      );

    // User Filter
    this.filteredUserOptions = this.userControl.valueChanges
      .pipe(
        debounceTime(200),
        distinctUntilChanged(),
        switchMap((str: string) => this.userService.search(str))
      );
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
      radius: this.radius
    });
  }

  filterMes(): void {
    this.messageService.updateMessageFilter({
      categoryMes: this.messageForm.get('categoryMes').value,
      hashtag: this.hashtagSelection,
      user: this.userSelection,
      time: this.messageForm.get('time').value,
      page: 0,
      size: 10
    });
    this.sidebarService.changeVisibilityAndFocus({isVisible: true});
    this.sidebarActive = true;
    this.router.navigate(['filter/messages']);
  }

  // Autocomplete Methods

  selectedHashtagOption(event) {
    this.hashtagSelection = event.option.value;
  }

  selectedUserOption(event) {
    this.userSelection = event.option.value;
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
      user: new FormControl(''),
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

  onSidebarActive(sidebarActive: boolean) {
    this.sidebarActive = sidebarActive;
  }

  changeState() {
    this.disabled = !this.disabled;
    this.radius = 0;
  }
}
