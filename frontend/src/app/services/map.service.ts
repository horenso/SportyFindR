import {Injectable, NgZone} from '@angular/core';
import {Subject} from 'rxjs';
import {MLocation} from '../util/m-location';
import {SidebarService} from './sidebar.service';
import {Map, Marker} from 'leaflet';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class MapService {

  public map: Map;
  public creationMarker: Marker = null;

  private addMarkerSubject = new Subject<MLocation>();
  public addMarkerObservable = this.addMarkerSubject.asObservable();
  private locationClickedSubject = new Subject<MLocation>();
  public locationClickedObservable = this.locationClickedSubject.asObservable();

  constructor(private sidebarService: SidebarService, private ngZone: NgZone, private router: Router) {
  }

  public addMarkerToLocations(markerLocation: MLocation) {
    this.setClickFunction(markerLocation);
    this.addMarkerSubject.next(markerLocation);
  }

  public setClickFunction(markerLocation: MLocation) {
    markerLocation.on('click', () => {
      this.onMarkerClick(markerLocation);
    });
  }

  private onMarkerClick(markerLocation: MLocation) {
    console.log(markerLocation.id);
    this.ngZone.run(() => {
      this.router.navigate(['locations', markerLocation.id]);
    });
    this.clickedOnLocation(markerLocation);
  }

  public clickedOnLocation(markerLocation: MLocation) {
    this.locationClickedSubject.next(markerLocation);
    this.sidebarService.markerLocation = markerLocation;
  }

  public getCreationMarker(): Marker {
    this.creationMarker = new Marker(this.map.getCenter(), {draggable: true});
    this.creationMarker.addTo(this.map).on('click', () => {
    });
    return this.creationMarker;
  }

  public destroyCreationMarker(): void {
    if (this.creationMarker !== null) {
      this.creationMarker.removeFrom(this.map);
    }
    this.creationMarker = null;
  }
}
