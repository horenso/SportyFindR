import {Component, OnDestroy, OnInit} from '@angular/core';
import {Category} from '../../dtos/category';
import {BehaviorSubject, Observable, Subscription} from 'rxjs';
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
import {map, startWith} from 'rxjs/operators';
import {SimpleHashtag} from '../../dtos/simpleHashtag';

@Component({
  selector: 'app-filter-main',
  templateUrl: './filter-main.component.html',
  styleUrls: ['./filter-main.component.scss']
})
export class FilterMainComponent implements OnInit, OnDestroy {

  categories: Category[];
  hashtags: SimpleHashtag[];
  radius: number = 0;
  strLoc: string;
  strMes: string;

  messageForm: FormGroup;
  locationForm: FormGroup;

  panelOpenState = false;

  disabled = true;

  filteredOptions: Observable<SimpleHashtag[]>;
  myControl = new FormControl();
  selection: string;

  sidebarActive: boolean = false;
  private subscription: Subscription;

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

  private _filter(name: string): SimpleHashtag[] {
    const filterValue = name.toLowerCase();

    return this.hashtags.filter(option => option.name.toLowerCase().indexOf(filterValue) === 0);
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

  selectedOption(event) {
    this.selection = event.option.value;
  }

  filterMes(): void {
    this.messageService.updateMessageFilter({
      categoryMes: this.messageForm.get('categoryMes').value,
      hashtag: this.selection,
      time: this.messageForm.get('time').value
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
        this.filteredOptions = this.myControl.valueChanges
          .pipe(
            startWith(''),
            map(value => typeof value === 'string' ? value : value.name),
            map(name => name ? this._filter(name) : this.hashtags.slice())
          );
      }, error => {
        this.notificationService.error('Error loading hashtags!');
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
