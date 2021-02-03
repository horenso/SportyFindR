import {Component, OnInit} from '@angular/core';
import {Category} from '../../dtos/category';
import {Observable} from 'rxjs';
import {FormBuilder, FormGroup} from '@angular/forms';
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

    this.filteredHashtagsMessages = this.locationForm.controls.hashtag.valueChanges.pipe(
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
    this.mapService.updateFilterLocation({
      categoryId: this.locationForm.value.categoryId,
      hashtag: this.locationForm.value.hashtag,
      radius: radius,
      radiusEnabled: this.locationForm.value.radiusEnabled,
      radiusBuffered: false});
  }

  filterMes(): void {
    this.messageService.updateMessageFilter({
      categoryMes: this.messageForm.value.categoryId,
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