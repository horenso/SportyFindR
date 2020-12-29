import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';
import {MLocation} from '../util/m-location';
import {SidebarService} from './sidebar.service';
import {Map} from 'leaflet';

@Injectable({
  providedIn: 'root'
})
export class MapService {

  public map: Map;
  private addMarkerSubject = new Subject<MLocation>();
  public addMarkerObservable = this.addMarkerSubject.asObservable();
  private locationClickedSubject = new Subject<MLocation>();
  public locationClickedObservable = this.locationClickedSubject.asObservable();

  constructor(private sidebarService: SidebarService) {
  }

  public addMarkerToLocations(markerLocation: MLocation) {
    this.addMarkerSubject.next(markerLocation);
  }

  public clickedOnLocation(markerLocation: MLocation) {
    this.locationClickedSubject.next(markerLocation);
    this.sidebarService.markerLocation = markerLocation;
  }
}
