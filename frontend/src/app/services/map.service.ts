import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {LayerGroup, Map, Marker} from 'leaflet';
import {LocationService} from './location.service';
import {Location} from '../dtos/location';
import {MarkerLocation} from '../util/marker-location';
import {MapSidebarComponent} from '../components/map-sidebar/map-sidebar.component';
import {SpotService} from './spot.service';
import {Spot} from '../dtos/spot';
import {MapComponent} from '../components/map/map.component';

@Injectable({
  providedIn: 'root'
})
export class MapService {

  private map = new BehaviorSubject<Map>(null); // this value should be set by the Map Component right in time
  public map$ = this.map.asObservable();
  private spots: Observable<Spot[]>;
  // tslint:disable-next-line:max-line-length
  private locationLayerGroup = new BehaviorSubject<LayerGroup<Marker>>(new LayerGroup<Marker>()); // this value should be set by the Map Component right in time
  public locationLayerGroup$ = this.locationLayerGroup.asObservable();

  private locMarker = new Subject<Marker>();
  public locMarker$ = this.locMarker.asObservable();

  constructor(private locationService: LocationService, private spotService: SpotService) { }

  public setMap(map: Map) {
    this.map.next(map);
  }

  public initLayers() {
    this.locationService.getAllLocations().subscribe(
      (locations: Location[]) => {
        this.convertLocations(locations);
      },
      error => {
        console.log('Error retrieving locations from backend: ' + error);
      }
    );
  }

  public addMarkerToLocations(markerLocation: MarkerLocation) {
    this.locMarker.next(markerLocation.on('click', () => {this.onMarkerClick(markerLocation); } ));
  }

  private convertLocations(locations: Location[]) {
    const locMarkerGroup: LayerGroup<MarkerLocation> = new LayerGroup<MarkerLocation>();
    locations.forEach(
      (loc: Location) => {
        const markerLocation = new MarkerLocation(loc);
        locMarkerGroup.addLayer(markerLocation.on('click', () => {this.onMarkerClick(markerLocation); } ));
      }
    );
    this.locationLayerGroup.next(locMarkerGroup);
  }
  public onMarkerClick(mLoc: MarkerLocation) {
    console.log('1');
    this.spots = this.spotService.getSpotsByLocation(mLoc.id);
    console.log(this.spots);
  }
}
