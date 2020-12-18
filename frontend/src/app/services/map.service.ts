import {EventEmitter, Injectable} from '@angular/core';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {LayerGroup, Map, Marker} from 'leaflet';
import {LocationService} from './location.service';
import {Location} from '../dtos/location';
import {MarkerLocation} from '../util/marker-location';
import {SpotService} from './spot.service';
import {Spot} from '../dtos/spot';
import {SidebarActionService} from './sidebar-action.service';

@Injectable({
  providedIn: 'root'
})
export class MapService {

  private map = new BehaviorSubject<Map>(null); // this value should be set by the Map Component right in time
  public map$ = this.map.asObservable();

  // this value should be set by the Map Component right in time
  private locationLayerGroup = new BehaviorSubject<LayerGroup<Marker>>(new LayerGroup<Marker>());
  private spots = new BehaviorSubject<Spot[]>(null);
  public spots$ = this.spots.asObservable();
  // tslint:disable-next-line:max-line-length
  public locationLayerGroup$ = this.locationLayerGroup.asObservable();

  private locMarker = new Subject<Marker>();
  public locMarker$ = this.locMarker.asObservable();

  private markerClicked = new Subject<number>();
  public markerClicked$ = this.markerClicked.asObservable();

  // tslint:disable-next-line:max-line-length
  constructor(
    private locationService: LocationService,
    private spotService: SpotService,
    private sidebarActionService: SidebarActionService)
    { }

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
    this.locMarker.next(markerLocation.on('click', () => {
      this.onMarkerClick(markerLocation);
    }));
  }

  private convertLocations(locations: Location[]) {
    const locMarkerGroup: LayerGroup<MarkerLocation> = new LayerGroup<MarkerLocation>();
    locations.forEach(
      (loc: Location) => {
        const markerLocation = new MarkerLocation(loc);
        locMarkerGroup.addLayer(markerLocation.on('click', () => {
          this.onMarkerClick(markerLocation);
        }));
      }
    );
    this.locationLayerGroup.next(locMarkerGroup);
  }

  private onMarkerClick(mLoc: MarkerLocation) {
    // console.log(mLoc.id);
    // this.sidebarActionService.setActionShowSpotsLoc();
    this.markerClicked.next(mLoc.id);
  }
}
