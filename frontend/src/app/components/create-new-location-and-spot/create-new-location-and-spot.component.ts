import {Component, OnDestroy, OnInit} from '@angular/core';
import {Marker} from 'leaflet';
import {MapService} from '../../services/map.service';
import {SidebarService} from '../../services/sidebar.service';
import {MLocSpot} from '../../util/m-loc-spot';
import {Router} from '@angular/router';
import {IconType, MLocation} from 'src/app/util/m-location';
import {SpotService} from 'src/app/services/spot.service';
import {NotificationService} from 'src/app/services/notification.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-create-new-location-and-spot',
  templateUrl: './create-new-location-and-spot.component.html',
  styleUrls: ['./create-new-location-and-spot.component.scss']
})
export class CreateNewLocationAndSpotComponent implements OnInit, OnDestroy {

  private subscription: Subscription;

  marker: Marker;

  constructor(
    private mapService: MapService,
    private sidebarService: SidebarService,
    private spotService: SpotService,
    private router: Router,
    private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    if (this.sidebarService.markerLocation != null) {
      this.sidebarService.markerLocation.changeIcon(IconType.Default);
    }
    this.marker = this.mapService.addDraggableMarker();
  }

  ngOnDestroy() {
    this.mapService.removeDraggableMarker();
    if (this.subscription != null) {
      this.subscription.unsubscribe();
    }
  }

  saveSpot(newSpot: MLocSpot) {
    newSpot.markerLocation = new MLocation(null, this.marker.getLatLng().lat, this.marker.getLatLng().lng);
    this.subscription = this.spotService.create(newSpot).subscribe(
      result => {
        const newMarkerLocation = result.markerLocation;
        this.mapService.addMarkerToLocations(newMarkerLocation);
        this.mapService.removeDraggableMarker();
        this.router.navigate(['..']);
        this.sidebarService.changeVisibilityAndFocus({isVisible: false});
        this.notificationService.success(`New Location with Spot ${result.name} saved.`);
      }, error => {
        this.notificationService.error(NotificationService.errorSavingSpot);
      }
    );
  }

  cancel() {
    this.router.navigate(['..']);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }
}
