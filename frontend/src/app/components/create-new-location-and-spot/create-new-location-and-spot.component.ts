import {Component, OnDestroy, OnInit} from '@angular/core';
import {Marker} from 'leaflet';
import {MapService} from '../../services/map.service';
import {SpotService} from '../../services/spot.service';
import {SidebarService} from '../../services/sidebar.service';
import {CategoryService} from '../../services/category.service';
import {MLocation} from '../../util/m-location';
import {MLocSpot} from '../../util/m-loc-spot';
import {Router} from '@angular/router';

@Component({
  selector: 'app-create-new-location-and-spot',
  templateUrl: './create-new-location-and-spot.component.html',
  styleUrls: ['./create-new-location-and-spot.component.scss']
})
export class CreateNewLocationAndSpotComponent implements OnInit, OnDestroy {

  marker: Marker;
  spot: MLocSpot;
  markerLocation: MLocation;

  constructor(
    private mapService: MapService,
    private spotService: SpotService,
    private categoryService: CategoryService,
    private sidebarService: SidebarService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.marker = this.mapService.getCreationMarker();
    this.markerLocation = this.sidebarService.markerLocation;
  }

  saveSpot(newSpot: MLocSpot) {
    console.log(newSpot);
    const newMarkerLocation = newSpot.markerLocation;
    this.mapService.addMarkerToLocations(newMarkerLocation);
    this.mapService.destroyCreationMarker();
    this.router.navigate(['..']);
  }

  cancel() {
    this.router.navigate(['..']);
  }

  ngOnDestroy() {
    this.mapService.destroyCreationMarker();
  }
}
