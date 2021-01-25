import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {control, Layer, LayerGroup, Map, tileLayer, Point, latLng, LatLng, LatLngBounds, Circle} from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {MapService} from 'src/app/services/map.service';
import {SidebarService, VisibilityFocusChange} from 'src/app/services/sidebar.service';
import {MLocation} from '../../util/m-location';
import {SubSink} from 'subsink';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, OnDestroy {

  @Output() mLocClicked = new EventEmitter();

  private subs = new SubSink();

  private circle;

  map: Map;
  leafletOptions = {
    center: [48.208174, 16.37819],
    zoom: 13,
    zoomDelta: 0.5,
    wheelPxPerZoomLevel: 90,
    zoomSnap: 0,
    cursor: true,
    minZoom: 1,
    maxZoom: 20,
  };
  private locationList: MLocation[];
  private locMarkerGroup: LayerGroup<MLocation> = new LayerGroup<MLocation>();

  private worldMap = 'https://stamen-tiles-{s}.a.ssl.fastly.net/watercolor/{z}/{x}/{y}.jpg';
//  private basemap = 'https://maps{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpg';
// high def but jpg, so no background layer is possible
  private basemap = 'https://maps{s}.wien.gv.at/basemap/geolandbasemap/normal/google3857/{z}/{y}/{x}.png';


  layers: Layer[] = [
    tileLayer(this.worldMap, {
      attribution: 'World Map tiles by <a href="http://stamen.com">Stamen Design</a>, <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a> &mdash; Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      subdomains: 'abcd',
      minZoom: 1,
      maxZoom: 16,
    }),
    tileLayer(this.basemap, {
      minZoom: 1,
      maxZoom: 20,
      attribution: 'Data Source Austria: <a href="https://www.basemap.at">basemap.at</a>',
      subdomains: ['', '1', '2', '3', '4'],
      bounds: [[46.35877, 8.782379], [49.037872, 17.189532]]
    })
  ];

  constructor(
    private locationService: LocationService,
    private mapService: MapService,
    private sidebarService: SidebarService) {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  onMapReady(map: Map) {
    control.scale({position: 'bottomleft', metric: true, imperial: false}).addTo(map);

    this.map = map;
    this.mapService.map = map;

    this.getLocationsAndConvertToLayerGroup();
    this.subs.add(this.mapService.addMarkerObservable.subscribe(markerLocation => {
      this.locationList.push(markerLocation);
      this.locMarkerGroup.addLayer(markerLocation);
    }));

    this.subs.add(this.mapService.removeMarkerLocObservable.subscribe(idToRemove => {
      this.removeMLocation(idToRemove);
    }));

    this.subs.add(this.sidebarService.changeVisibilityAndFocusObservable.subscribe(change => {
      this.changeVisibilityAndFocus(change);
    }));

    this.subs.add(this.mapService.updateLocationFilterObservable.subscribe(change => {
      this.locationService.filterLocation({
        categoryLoc: change.categoryLoc,
        latitude: this.map.getCenter().lat,
        longitude: this.map.getCenter().lng,
        radius: change.radius
      }).subscribe(
        (result: MLocation[]) => {
          this.locationList = result;
          this.addMarkers();
          if (this.circle != null) {
            this.map.removeLayer(this.circle);
          }
          this.circle = new Circle(this.map.getCenter(), change.radius * 1000).addTo(this.map);
        }
      );
    }));

    this.map.on('moveend', () => { this.changeLocationView(); });

    setTimeout(() => this.map.invalidateSize({pan: false}));
  }

  private changeLocationView() {
    if (this.circle != null) {
      this.map.removeLayer(this.circle);
    }
    console.log('first', this.layers);
    const width = this.map.getBounds().getEast() - this.map.getBounds().getWest();
    // add max south north
    const radius = (width / 2) * 111;
    this.subs.add(this.locationService.filterLocation({
      categoryLoc: 0,
      latitude: this.map.getCenter().lat,
      longitude: this.map.getCenter().lng,
      radius: radius
    }).subscribe(
      (result: MLocation[]) => {
        this.locationList = result;
        console.log('LOCATION LIST 1: ', this.locationList);
        // this.circle = new Circle(this.map.getCenter(), radius * 1000).addTo(this.map);
        this.addMarkers();
    }));
}
/*
  public viewLocations(radius: number) {
    radius = radius / 111;
    const corner1 = new LatLng(this.map.getCenter().lat + radius, this.map.getCenter().lng + radius);
    const corner2 = new LatLng(this.map.getCenter().lat - radius, this.map.getCenter().lng - radius);
    const bounds = new LatLngBounds(corner1, corner2);
    this.map.fitBounds(bounds);
  }
 */

  private getLocationsAndConvertToLayerGroup() {
    const width = this.map.getBounds().getEast() - this.map.getBounds().getWest();
    // add max south north
    const radius = (width / 2) * 111;
    this.subs.add(this.locationService.filterLocation({
      categoryLoc: 0,
      latitude: this.map.getCenter().lat,
      longitude: this.map.getCenter().lng,
      radius: radius
    }).subscribe(
      (result: MLocation[]) => {
        this.locationList = result;
        this.addMarkers();
      },
      error => {
        console.log('Error retrieving locations from backend: ', error);
      }
    ));
  }

  private addMarkers(): void {
    if (this.locMarkerGroup.getLayers().length > 0) {
      this.locMarkerGroup.clearLayers();
    }
    console.log(this.locMarkerGroup);
    this.locationList.forEach(
      (mLoc: MLocation) => {
        this.mapService.setClickFunction(mLoc);
        this.locMarkerGroup.addLayer(mLoc);
      }
    );
    this.layers.push(this.locMarkerGroup);
  }


  public removeMLocation(id: number) {
    const found = this.locationList.find(ele => ele.id === id);
    if (found != null) {
      this.locMarkerGroup?.removeLayer(found);
    }
  }

  private changeVisibilityAndFocus(change: VisibilityFocusChange): void {
    if ((!this.sidebarService.isSidebarClosed()) === change.isVisible) { // If the visibility stayed the same
      if (change.locationInFocus != null) {
        this.map.setView(change.locationInFocus.getLatLng(), this.map.getZoom());
      }
    } else {
      if (change.locationInFocus != null) {
        const latLng = change.locationInFocus.getLatLng();
        const point = this.map.latLngToContainerPoint(latLng);
        const newPoint = new Point(point.x + 250, point.y);
        const newLatLng = this.map.containerPointToLatLng(newPoint);
        this.map.setView(newLatLng, this.map.getZoom());
      }
      setTimeout(() => {
        console.log('Resizing Map');
        this.map.invalidateSize({pan: false});
        if (change.isVisible) {
          this.sidebarService.setSidebarStateOpen();
        } else {
          this.sidebarService.setSidebarStateClosed();
        }
      }, 300);
    }
  }
}
