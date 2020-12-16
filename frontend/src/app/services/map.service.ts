import { Injectable } from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';
import {LayerGroup, Map, Marker} from 'leaflet';
import {LocationService} from "./location.service";
import {Location} from "../dtos/location";

@Injectable({
  providedIn: 'root'
})
export class MapService {

  private map = new BehaviorSubject<Map>(null); // this value should be set by the Map Component right in time
  public map$ = this.map.asObservable();

  private locationLayerGroup = new BehaviorSubject<LayerGroup<Marker>>(new LayerGroup<Marker>()); // this value should be set by the Map Component right in time
  public locationLayerGroup$ = this.locationLayerGroup.asObservable();

  private locMarker = new Subject<Marker>();
  public locMarker$ = this.locMarker.asObservable();

  constructor(private locationService: LocationService) { }

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
    )
  }

  public addMarkerToLocations(locMarker: Marker) {
    this.locMarker.next(locMarker);
  }

  private convertLocations(locations: Location[]) {
    const locMarkerGroup: LayerGroup<Marker> = new LayerGroup<Marker>();
    locations.forEach(
      (loc: Location) => {
        locMarkerGroup.addLayer(new Marker([loc.latitude, loc.longitude]));
      }
    )
    this.locationLayerGroup.next(locMarkerGroup);
  }
}
