import {Component, OnDestroy, OnInit} from '@angular/core';
import {Category} from '../../dtos/category';
import {Observable, Subscription} from 'rxjs';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {CategoryService} from '../../services/category.service';
import {NotificationService} from '../../services/notification.service';
import {Router} from '@angular/router';
import {MessageService} from '../../services/message.service';
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
  hashtagsMes: SimpleHashtag[];
  hashtagsLoc: SimpleHashtag[];

  users: SimpleUser[];
  radius: number = 0;
  strLoc: string;
  strMes: string;

  messageForm: FormGroup;
  locationForm: FormGroup;

  panelOpenState = false;

  disabled = true;

  filteredHashtagMesOptions: Observable<SimpleHashtag[]>;
  hashtagMesControl = new FormControl();
  hashtagMesSelection: string;

  filteredHashtagLocOptions: Observable<SimpleHashtag[]>;
  hashtagLocControl = new FormControl();
  hashtagLocSelection: string;

  filteredUserOptions: Observable<SimpleUser[]>;
  userControl = new FormControl();
  userSelection: string;

  private subscription: Subscription;

  public minDistance: number = 800;
  public maxDistance: number = 10000;

  constructor(
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    private messageService: MessageService,
    private hashtagService: HashtagService,
    private sidebarService: SidebarService,
    private mapService: MapService,
    private notificationService: NotificationService,
    private router: Router,
    private userService: UserService) {
  }

  ngOnInit(): void {
    this.getAllCategories();
    this.buildMessageForm();
    this.buildLocationForm();

    // Hashtag Location Filter
    this.filteredHashtagLocOptions = this.hashtagMesControl.valueChanges
      .pipe(
        debounceTime(200),
        distinctUntilChanged(),
        switchMap((str: string) => this.hashtagService.search(str))
      );

    // Hashtag Message Filter
    this.filteredHashtagMesOptions = this.hashtagMesControl.valueChanges
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

  filterLoc(): void {
    let radius = this.locationForm.value.radius;
    if (isNaN(radius)) {
      radius = null;
    }
    this.mapService.updateFilterLocation({
      categoryId: this.locationForm.value.categoryId,
      hashtag: this.hashtagLocSelection,
      radius: radius,
      radiusEnabled: this.locationForm.value.radiusEnabled,
      radiusBuffered: false});
  }

  filterMes(): void {
    this.messageService.updateMessageFilter({
      categoryMes: this.messageForm.get('categoryMes').value,
      hashtag: this.hashtagMesSelection,
      user: this.userSelection,
      time: this.messageForm.get('time').value,
      page: 0,
      size: 10
    });
    this.sidebarService.changeVisibilityAndFocus({isVisible: true});
    this.router.navigate(['filter/messages']);
  }

  // Autocomplete Methods

  selectedHashtagMesOption(event) {
    this.hashtagMesSelection = event.option.value;
  }

  // Autocomplete Methods

  selectedHashtagLocOption(event) {
    this.hashtagLocSelection = event.option.value;
  }

  selectedUserOption(event) {
    this.userSelection = event.option.value;
  }

  buildLocationForm(): void {
    this.locationForm = this.formBuilder.group({
      categoryId: [null],
      radius: [{value: this.minDistance, disabled: true}],
      radiusEnabled: [false]
    });
  }

  buildMessageForm(): void {
    this.messageForm = this.formBuilder.group({
      categoryMes: [''],
      user: [''],
      hashtag: [''],
      time: ['']
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

  resetLocationFilter(): void {
    this.locationForm.reset();
    this.locationForm.controls['radius'].disable();
    this.locationForm.controls['radius'].setValue(this.minDistance);
    this.mapService.updateFilterLocation({
      categoryId: null, hashtag: null, radiusEnabled: false, radius: null, coordinates: null, radiusBuffered: false
    });
  }

  resetMessageFilter(): void {
    this.messageForm.reset();
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }

  toggleIncludeRadius() {
    if (this.locationForm.controls.radius.enabled) {
      this.locationForm.controls.radius.disable();
    } else {
      this.locationForm.controls.radius.enable();
    }
  }
}
