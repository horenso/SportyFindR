import {EventEmitter, Injectable, NgZone} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {IconType, MLocation} from '../util/m-location';
import {SidebarService} from './sidebar.service';
import {Icon, Map, marker, Marker} from 'leaflet';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class MapService {

  // private selectedMarker: MLocation = null;

  public map: Map;
  public creationMarker: Marker = null;

  private addMarkerSubject = new Subject<MLocation>();
  public addMarkerObservable = this.addMarkerSubject.asObservable();

  private locationClickedSubject = new Subject<MLocation>();
  public locationClickedObservable = this.locationClickedSubject.asObservable();

  private removeMarkerLocSubject = new Subject<number>();
  public removeMarkerLocObservable = this.removeMarkerLocSubject.asObservable();

  private resetAllMarkerIconsSubject = new Subject<any>();
  public resetAllMarkerIconsObservable = this.resetAllMarkerIconsSubject.asObservable();

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
    console.log('clicked on marker: ' + markerLocation.id);
    this.sidebarService.changeVisibilityAndFocus({isVisible: true, locationInFocus: markerLocation});

    if (this.sidebarService.markerLocation != null) {
      this.sidebarService.markerLocation.changeIcon(IconType.Default);
    }
    markerLocation.changeIcon(IconType.Edit);
    this.sidebarService.markerLocation = markerLocation;

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
    this.creationMarker.addTo(this.map).on('click', () => {});
    this.creationMarker.setIcon(MLocation.iconNew);
    return this.creationMarker;
  }

  public destroyCreationMarker(): void {
    if (this.creationMarker !== null) {
      this.creationMarker.removeFrom(this.map);
    }
    this.creationMarker = null;
  }

  public resetAllMarkerIcons(): void {
    this.resetAllMarkerIconsSubject.next({});
  }

  public removeMarkerLocation(locationId: number): void {
    this.removeMarkerLocSubject.next(locationId);
  }
}
