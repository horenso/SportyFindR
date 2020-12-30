import {Component, OnDestroy, OnInit} from '@angular/core';
import {Marker} from 'leaflet';
import {MapService} from '../../services/map.service';
import {SidebarService} from '../../services/sidebar.service';
import {MLocSpot} from '../../util/m-loc-spot';
import {Router} from '@angular/router';

@Component({
  selector: 'app-create-new-location-and-spot',
  templateUrl: './create-new-location-and-spot.component.html',
  styleUrls: ['./create-new-location-and-spot.component.scss']
})
export class CreateNewLocationAndSpotComponent implements OnInit, OnDestroy {

  marker: Marker;

  constructor(
    private mapService: MapService,
    private sidebarService: SidebarService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.marker = this.mapService.getCreationMarker();
  }

  saveSpot(newSpot: MLocSpot) {
    console.log(newSpot);
    const newMarkerLocation = newSpot.markerLocation;
    this.mapService.addMarkerToLocations(newMarkerLocation);
    this.mapService.destroyCreationMarker();
    this.router.navigate(['..']);
    this.sidebarService.changeVisibility(false);
  }

  cancel() {
    this.router.navigate(['..']);
    this.sidebarService.changeVisibility(false);
  }

  ngOnDestroy() {
    this.mapService.destroyCreationMarker();
  }
}
