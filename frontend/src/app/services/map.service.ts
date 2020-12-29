import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';
import {MarkerLocation} from '../util/marker-location';
import {SidebarService} from './sidebar.service';
import {Map} from 'leaflet';

@Injectable({
  providedIn: 'root'
})
export class MapService {

  constructor(
    private sidebarService: SidebarService) {
  }

  public map: Map;

  private addMarkerSubject = new Subject<MarkerLocation>();
  private locationClickedSubject = new Subject<MarkerLocation>();

  public addMarkerObservable = this.addMarkerSubject.asObservable();
  public locationClickedObservable = this.locationClickedSubject.asObservable();

  public addMarkerToLocations(markerLocation: MarkerLocation) {
    this.addMarkerSubject.next(markerLocation);
  }

  public clickedOnLocation(markerLocation: MarkerLocation) {
    this.locationClickedSubject.next(markerLocation);
  }
}
