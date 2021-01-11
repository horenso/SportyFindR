import {Component, OnDestroy, OnInit} from '@angular/core';
import {Spot} from '../../dtos/spot';
import {SpotService} from 'src/app/services/spot.service';
import {SidebarService} from 'src/app/services/sidebar.service';
import {Subscription} from 'rxjs';
import {MLocSpot} from '../../util/m-loc-spot';
import {ActivatedRoute, Router} from '@angular/router';
import {parsePositiveInteger} from '../../util/parse-int';
import {IconType} from 'src/app/util/m-location';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-location-view',
  templateUrl: './location-view.component.html',
  styleUrls: ['./location-view.component.scss']
})
export class LocationViewComponent implements OnInit, OnDestroy {

  locationId: number = null;

  public spots: MLocSpot[] = [];

  private locationClickedSubscription: Subscription;
  private getSpotsSubscription: Subscription;

  constructor(
    private spotService: SpotService,
    private sidebarService: SidebarService,
    private route: Router,
    private activedRoute: ActivatedRoute,
    private notificationService: NotificationService,
    private router: Router) {}

  ngOnInit(): void {
    this.activedRoute.params.subscribe(params => {

      this.locationId = parsePositiveInteger(params.locId);

      if (isNaN(this.locationId)) {
        this.notificationService.navigateHomeAndShowError(NotificationService.locIdNotInt);
      } else {
        this.spotService.getByLocationId(this.locationId).subscribe(
          result => {
            this.spots = result;
          },
          error => {
            this.notificationService.navigateHomeAndShowError('Error loading location!');
          }
        );
      }
    });
  }

  onClose(): void {
    this.route.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }

  onSelectedSpot(spot: Spot) {
    this.route.navigate(['locations', this.locationId, 'spots', spot.id]);
  }

  onCreateSpot() {
    this.route.navigate(['locations', this.locationId, 'spots', 'new']);
  }

  ngOnDestroy() {
    this.getSpotsSubscription?.unsubscribe();
    this.locationClickedSubscription?.unsubscribe();
  }
}
