import {Injectable, NgZone} from '@angular/core';
import {Subject} from 'rxjs';
import {IconType, MLocation} from '../util/m-location';
import {SidebarService} from './sidebar.service';
import {Map, Marker, Point} from 'leaflet';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class MapService {

  public map: Map;
  public creationMarker: Marker = null;

  private addMarkerSubject = new Subject<MLocation>();
  public addMarkerObservable = this.addMarkerSubject.asObservable();

  private removeMarkerLocSubject = new Subject<number>();
  public removeMarkerLocObservable = this.removeMarkerLocSubject.asObservable();

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
    console.log('onMarkerClick()');
    console.log(markerLocation);

    this.sidebarService.changeVisibilityAndFocus({isVisible: true, locationInFocus: markerLocation});

    if (this.sidebarService.markerLocation != null) {
      this.sidebarService.markerLocation.changeIcon(IconType.Default);
    }
    markerLocation.changeIcon(IconType.Edit);
    this.sidebarService.markerLocation = markerLocation;

    this.ngZone.run(() => {
      this.router.navigate(['locations', markerLocation.id]);
    });
  }

  public getCreationMarker(): Marker {
    let latLng = this.map.getCenter();

    if (this.sidebarService.isSidebarClosed()) {
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

  public removeMarkerLocation(locationId: number): void {
    this.removeMarkerLocSubject.next(locationId);
  }
}
