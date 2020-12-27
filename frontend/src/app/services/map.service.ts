import {Injectable} from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';
import {LayerGroup, Map, Marker} from 'leaflet';
import {LocationService} from './location.service';
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
  // tslint:disable-next-line:max-line-length
  private locationLayerGroup = new BehaviorSubject<LayerGroup<Marker>>(new LayerGroup<Marker>()); // this value should be set by the Map Component right in time
  public locationLayerGroup$ = this.locationLayerGroup.asObservable();
  private spots = new BehaviorSubject<Spot[]>(null);
  public spots$ = this.spots.asObservable();

  private locMarker = new Subject<Marker>();
  public locMarker$ = this.locMarker.asObservable();

  // tslint:disable-next-line:max-line-length
  constructor(private locationService: LocationService, private spotService: SpotService, private sidebarActionService: SidebarActionService) {
  }

  public setMap(map: Map) {
    this.map.next(map);
  }

  public initLayers() {
    this.locationService.getAllMarkerLocations().subscribe(
      (markerLocations: MarkerLocation[]) => {
        this.convertLocations(markerLocations);
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

  private convertLocations(mLocs: MarkerLocation[]) {
    const locMarkerGroup: LayerGroup<MarkerLocation> = new LayerGroup<MarkerLocation>();
    mLocs.forEach(
      (mLoc: MarkerLocation) => {
        locMarkerGroup.addLayer(mLoc.on('click', () => {
          this.onMarkerClick(mLoc);
        }));
      }
    );
    this.locationLayerGroup.next(locMarkerGroup);
  }

  private onMarkerClick(mLoc: MarkerLocation) {
    this.spotService.getSpotsByLocation(mLoc.id).subscribe((spots: Spot[]) => {
        this.spots.next(spots);
      },
      (error) => {
        console.log(error);
      }
    );
    console.log(mLoc.id);
    this.sidebarActionService.setActionShowSpotsLoc();
  }
}
