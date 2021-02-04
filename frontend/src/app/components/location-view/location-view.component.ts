import {Component, OnDestroy, OnInit} from '@angular/core';
import {Spot} from '../../dtos/spot';
import {SpotService} from 'src/app/services/spot.service';
import {SidebarService} from 'src/app/services/sidebar.service';
import {MLocSpot} from '../../util/m-loc-spot';
import {ActivatedRoute, Router} from '@angular/router';
import {parsePositiveInteger} from '../../util/parse-int';
import {IconType} from 'src/app/util/m-location';
import {NotificationService} from 'src/app/services/notification.service';
import {SubSink} from 'subsink';
import { FilterService } from 'src/app/services/filter.service';
import { FilterLocation } from 'src/app/dtos/filter-location';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-location-view',
  templateUrl: './location-view.component.html',
  styleUrls: ['./location-view.component.scss']
})
export class LocationViewComponent implements OnInit, OnDestroy {

  public spots: MLocSpot[] = [];

  private locationId: number = null;

  private currentFilter: FilterLocation;

  public buttonColor: string;

  private subs = new SubSink();

  constructor(
    private spotService: SpotService,
    private sidebarService: SidebarService,
    private filterService: FilterService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private notificationService: NotificationService,
    private authService: AuthService) {
  }

  ngOnInit(): void {
    this.subs.add(this.activatedRoute.params.subscribe(params => {

      this.locationId = parsePositiveInteger(params.locId);

      if (isNaN(this.locationId)) {
        this.notificationService.navigateHomeAndShowError(NotificationService.locIdNotInt);
      } else {
        this.subs.add(this.filterService.filterLocationObservable.subscribe(filter => {
          this.currentFilter = filter;
          this.getSpots();
        }));
      }
    }));
  }

  ngOnDestroy(): void {
    this.subs?.unsubscribe();
  }

  onClose(): void {
    this.router.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }

  onSelectedSpot(spot: Spot): void {
    this.router.navigate(['locations', this.locationId, 'spots', spot.id]);
  }

  onCreateSpot(): void {
    this.router.navigate(['locations', this.locationId, 'spots', 'new']);
  }

  isFilterActive(): boolean {
    return ;
  }

  updateButtonColor(): string {
    if (this.currentFilter.hashtag != null || this.currentFilter.categoryId != null) {
      return 'warn';
    } else {
      return 'primary';
    }
  }

  private getSpots(): void {
    this.subs.add(this.spotService.getByLocationId(
      this.locationId,
      this.filterService.currentFilterLocation.hashtag,
      this.filterService.currentFilterLocation.categoryId).subscribe(
      result => {
        this.buttonColor = this.updateButtonColor();
        this.spots = result;
      },
      error => {
        this.notificationService.navigateHomeAndShowError('Error loading location!');
      }
    ));
  }
}
