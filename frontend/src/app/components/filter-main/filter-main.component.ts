import {Component, OnInit} from '@angular/core';
import {Category} from '../../dtos/category';
import {Observable} from 'rxjs';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CategoryService} from '../../services/category.service';
import {NotificationService} from '../../services/notification.service';
import {Router} from '@angular/router';
import {HashtagService} from '../../services/hashtag.service';
import {SidebarService} from '../../services/sidebar.service';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';
import {SimpleHashtag} from '../../dtos/simple-hashtag';
import {SimpleUser} from '../../dtos/simple-user';
import {UserService} from '../../services/user.service';
import { now } from 'lodash';
import {AuthService} from '../../services/auth.service';
import {FilterService} from 'src/app/services/filter.service';

@Component({
  selector: 'app-filter-main',
  templateUrl: './filter-main.component.html',
  styleUrls: ['./filter-main.component.scss']
})
export class FilterMainComponent implements OnInit {

  categories: Category[];

  messageForm: FormGroup;
  locationForm: FormGroup;

  filteredHashtagsMessages: Observable<SimpleHashtag[]>;
  filteredHashtagsLocations: Observable<SimpleHashtag[]>;
  filteredUsers: Observable<SimpleUser[]>;

  panelOpenState = false;

  public minDistance: number = 800;
  public maxDistance: number = 10000;
  public maxDate: Date = new Date(Date.now());

  constructor(
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    private hashtagService: HashtagService,
    private sidebarService: SidebarService,
    private filterService: FilterService,
    private notificationService: NotificationService,
    private router: Router,
    private userService: UserService,
    private authService: AuthService) {
  }

  ngOnInit(): void {
    this.getAllCategories();
    this.buildMessageForm();
    this.buildLocationForm();

    this.filteredHashtagsLocations = this.locationForm.controls.hashtag.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((str: string) => this.hashtagService.search(str))
    );

    this.filteredHashtagsMessages = this.messageForm.controls.hashtag.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((str: string) => this.hashtagService.search(str))
    );

    this.filteredUsers = this.messageForm.controls.user.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((str: string) => this.userService.search(str))
    );
  }

  buildLocationForm(): void {
    this.locationForm = this.formBuilder.group({
      categoryId: [null],
      hashtag: [null],
      radius: [{value: this.minDistance, disabled: true}],
      radiusEnabled: [false]
    });
  }

  buildMessageForm(): void {
    this.messageForm = this.formBuilder.group({
      categoryId: [null],
      user: [null],
      hashtag: [null],
      time: [null]
    });
  }

  filterLoc(): void {
    let radius = this.locationForm.value.radius;
    if (isNaN(radius)) {
      radius = null;
    }
    this.filterService.updateFilterLocation({
      categoryId: this.locationForm.value.categoryId,
      hashtag: this.locationForm.value.hashtag,
      radius: radius,
      radiusEnabled: this.locationForm.value.radiusEnabled,
      radiusBuffered: false});
  }

  filterMes(): void {
    this.filterService.updateMessageFilter({
      categoryId: this.messageForm.value.categoryId,
      hashtag: this.messageForm.value.hashtag,
      user: this.messageForm.value.user,
      time: this.messageForm.value.time,
      page: 0,
      size: 10
    });
    this.sidebarService.changeVisibilityAndFocus({isVisible: true});
    this.router.navigate(['filter', 'messages']);
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
  }

  resetMessageFilter(): void {
    this.messageForm.reset();
  }

  toggleIncludeRadius() {
    if (this.locationForm.controls.radius.enabled) {
      this.locationForm.controls.radius.disable();
    } else {
      this.locationForm.controls.radius.enable();
    }
  }
}
