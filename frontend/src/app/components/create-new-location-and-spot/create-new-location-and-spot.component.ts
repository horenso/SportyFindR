import {Component, OnDestroy, OnInit} from '@angular/core';
import {Map, marker, Marker} from 'leaflet';
import {MapService} from '../../services/map.service';
import {Subscription} from 'rxjs';
import {SpotModel} from '../../dtos/spot';
import {SpotService} from '../../services/spot.service';
import {Location} from '../../dtos/location';
import {SidebarActionService} from '../../services/sidebar-action.service';

@Component({
  selector: 'app-create-new-location-and-spot',
  templateUrl: './create-new-location-and-spot.component.html',
  styleUrls: ['./create-new-location-and-spot.component.scss']
})
export class CreateNewLocationAndSpotComponent implements OnInit, OnDestroy {

  selectedCategory: String;
  categories: String[];
  subscription: Subscription;

  private map: Map;
  locMarker: Marker;

  spot: SpotModel;

  getMap() {
    this.subscription = this.mapService.map$.subscribe(
      map => {
        this.map = map;
        this.createMarker();
        this.initSpot();
      },
      error => {
        console.log('create-new-location-and-spot.component lost connection to map with error: ', + error);
      },
    );
  }

  createMarker() {
    this.locMarker = marker(this.map.getCenter(), {draggable: true});
    this.locMarker.addTo(this.map);
  }

  initSpot() {
    const location = new Location(null, this.locMarker.getLatLng().lat, this.locMarker.getLatLng().lng);
    this.spot = new SpotModel(null, '', '', location);
  }

  saveSpot() {
    this.spot.location = new Location(null, this.locMarker.getLatLng().lat, this.locMarker.getLatLng().lng);
    console.log('Creating new spot: ', this.spot);
    this.spotService.createSpot(this.spot);
    this.sidebarActionService.setActionSuccess();
  }

  cancel() {
    this.sidebarActionService.setActionCancelled();
  }

  constructor(
    private mapService: MapService,
    private spotService: SpotService,
    private sidebarActionService: SidebarActionService
  ) { }

  ngOnInit(): void {
    this.getMap();
  }

  ngOnDestroy() {
    this.locMarker.removeFrom(this.map);
    this.subscription.unsubscribe();
  }

}
