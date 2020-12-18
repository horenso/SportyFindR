import {Component, OnDestroy, OnInit} from '@angular/core';
import {Map, marker, Marker} from 'leaflet';
import {MapService} from '../../services/map.service';
import {Subscription} from 'rxjs';
import {Spot} from '../../dtos/spot';
import {SpotService} from '../../services/spot.service';
import {Location} from '../../dtos/location';
import {SidebarActionType, SidebarService} from '../../services/sidebar.service';
import {CategoryService} from '../../services/category.service';
import {MarkerLocation} from '../../util/marker-location';

@Component({
  selector: 'app-create-new-location-and-spot',
  templateUrl: './create-new-location-and-spot.component.html',
  styleUrls: ['./create-new-location-and-spot.component.scss']
})
export class CreateNewLocationAndSpotComponent implements OnInit, OnDestroy {

  locMarker: Marker;
  spot: Spot;
  location: Location;
  private categorySubscription: Subscription;
  private map: Map;

  constructor(
    private mapService: MapService,
    private spotService: SpotService,
    private categoryService: CategoryService,
    private sidebarService: SidebarService) {
  }

  ngOnInit(): void {
    this.map = this.mapService.map;
    this.createMarker();
    this.location = this.sidebarService.location;
  }

  saveSpot(newSpot: Spot) {
    console.log(newSpot);
    const newMarkerLocation = new MarkerLocation(newSpot.location);
    newMarkerLocation.addTo(this.map)
      .on('click', () => {
        this.mapService.clickedOnLocation(newMarkerLocation);
      });
    this.sidebarService.setAction(SidebarActionType.Success);
  }

  cancel() {
    this.sidebarService.setAction(SidebarActionType.Cancelled);
  }

  ngOnDestroy() {
    this.locMarker.removeFrom(this.map);
  }

  private createMarker() {
    this.locMarker = marker(this.map.getCenter(), {draggable: true});
    this.locMarker.addTo(this.map).on('click', () => {
      ;
    });
  }
}
