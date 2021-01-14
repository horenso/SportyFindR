import {Component, OnInit} from '@angular/core';
import {SidebarService} from '../../services/sidebar.service';
import {MLocation} from '../../util/m-location';
import {MLocSpot} from '../../util/m-loc-spot';
import {ActivatedRoute, Router} from '@angular/router';
import {parsePositiveInteger} from 'src/app/util/parse-int';
import {LocationService} from 'src/app/services/location.service';
import {SpotService} from 'src/app/services/spot.service';
import { NotificationService } from 'src/app/services/notification.service';


@Component({
  selector: 'app-create-new-spot',
  templateUrl: './create-new-spot.component.html',
  styleUrls: ['./create-new-spot.component.scss']
})
export class CreateNewSpotComponent implements OnInit {

  public locationId: number;
  public mLocation: MLocation;

  constructor(
    private sidebarService: SidebarService,
    private locationService: LocationService,
    private spotService: SpotService,
    private router: Router,
    private activedRoute: ActivatedRoute,
    private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.activedRoute.params.subscribe(params => {

      this.locationId = parsePositiveInteger(params.locId);

      if (isNaN(this.locationId)) {
        this.notificationService.navigateHomeAndShowError(NotificationService.locIdNotInt);
        return;
      }

      if (this.sidebarService.markerLocation != null && this.sidebarService.markerLocation.id === this.locationId) {
        this.mLocation = this.sidebarService.markerLocation;
      } else {
        this.locationService.getById(this.locationId).subscribe(
          result => {
            this.mLocation = result;
            this.sidebarService.markerLocation = result;
          }, error => {
            this.notificationService.navigateHomeAndShowError(NotificationService.errorLoadingLoc);
          }
        );
      }
    });
  }

  saveSpot(newSpot: MLocSpot) {
    newSpot.markerLocation = new MLocation(this.locationId, 0.0, 0.0);
    this.spotService.create(newSpot).subscribe(
      result => {
        this.router.navigate(['../../'], {relativeTo: this.activedRoute});
        this.notificationService.success(`New Spot ${result.name} saved.`);
      }, error => {
        this.notificationService.error(NotificationService.errorSavingSpot);
      });
  }

  cancel() {
    this.router.navigate(['locations', this.locationId]);
  }
}
