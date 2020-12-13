import {Component, OnDestroy, OnInit} from '@angular/core';
import {Layer, Map, marker, Marker} from 'leaflet';
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

  createSpot(spotName: string, spotDescription: string, spotCategory: string, spotLat: number, spotLng: number) {
    const location = new Location(null, spotLat, spotLng);
    this.spot = new SpotModel(null, spotName, spotDescription, location);
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
