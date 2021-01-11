import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { NotificationService } from 'src/app/services/notification.service';
import {SidebarService} from 'src/app/services/sidebar.service';
import {SpotService} from 'src/app/services/spot.service';
import {MLocSpot} from 'src/app/util/m-loc-spot';
import {parsePositiveInteger} from 'src/app/util/parse-int';

@Component({
  selector: 'app-edit-spot',
  templateUrl: './edit-spot.component.html',
  styleUrls: ['./edit-spot.component.scss']
})
export class EditSpotComponent implements OnInit {

  spot: MLocSpot = null;

  constructor(
    private activedRoute: ActivatedRoute,
    private sidebarService: SidebarService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private spotService: SpotService,
    private notificationService: NotificationService) {}

  ngOnInit(): void {
    if (this.sidebarService.spot != null) {
      this.spot = this.sidebarService.spot;
    } else {
      let spotId: number;
      this.activedRoute.params.subscribe(params => {
        spotId = parsePositiveInteger(params.spotId);
        if (isNaN(spotId)) {
          this.notificationService.error(NotificationService.spotIdNotInt);
          return;
        }

        this.spotService.getById(spotId).subscribe(
          result => {
            this.spot = result;
          }, error => {
            this.notificationService.error(NotificationService.errorLoadingSpot);
          }
        );
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }

  update(spot: MLocSpot): void {
    this.spotService.update(spot).subscribe(
      result => {
        this.notificationService.success(`Spot ${result.name} updated!`);
        this.sidebarService.spot = result;
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }, error => {
        this.notificationService.error(NotificationService.errorLoadingSpot);
      }
    );
  }
}
