import {EventEmitter, Injectable, NgZone} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {IconType, MLocation} from '../util/m-location';
import {SidebarService, SidebarState} from './sidebar.service';
import {Icon, LatLng, Map, marker, Marker, Point} from 'leaflet';
import {Router} from '@angular/router';

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
    let latLng = this.map.getCenter();

    if (this.sidebarService.sidebarState === SidebarState.Closed) {
      const point = this.map.latLngToContainerPoint(latLng);
      const newPoint = new Point(point.x - 250, point.y);
      latLng = this.map.containerPointToLatLng(newPoint);
    }

    // If a location is already selected, to prevent the marker from being exactly above
    latLng.lat += 0.001;
    latLng.lng += 0.001;

    this.creationMarker = new Marker(latLng, {
      draggable: true, 
      autoPan: true, 
      autoPanPadding: new Point(60, 60),
      zIndexOffset: 1000}
    );

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
