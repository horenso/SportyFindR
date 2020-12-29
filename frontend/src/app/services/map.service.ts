import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';
import {MLocation} from '../util/m-location';
import {SidebarService} from './sidebar.service';
import {Map} from 'leaflet';

@Injectable({
  providedIn: 'root'
})
export class MapService {

  constructor(private sidebarService: SidebarService) {
  }

  public map: Map;

  private addMarkerSubject = new Subject<MLocation>();
  private locationClickedSubject = new Subject<MLocation>();

  public addMarkerObservable = this.addMarkerSubject.asObservable();
  public locationClickedObservable = this.locationClickedSubject.asObservable();

  public addMarkerToLocations(markerLocation: MLocation) {
    this.addMarkerSubject.next(markerLocation);
  }

  public clickedOnLocation(markerLocation: MLocation) {
    this.locationClickedSubject.next(markerLocation);
    this.sidebarService.markerLocation = markerLocation;
  }
}
