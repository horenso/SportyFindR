import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {Circle, control, LatLng, Layer, LayerGroup, Map, Point, tileLayer} from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {MapService} from 'src/app/services/map.service';
import {SidebarService, VisibilityFocusChange} from 'src/app/services/sidebar.service';
import {MLocation} from '../../util/m-location';
import {SubSink} from 'subsink';
import {AuthService} from '../../services/auth.service';
import {FilterLocation} from 'src/app/dtos/filter-location';
import {FilterService} from 'src/app/services/filter.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, OnDestroy {

  @Output() mLocClicked = new EventEmitter();

  private subs = new SubSink();

  private circle: Circle;

  private filter: FilterLocation = {radiusEnabled: false, radiusBuffered: false};
  private isFilterBuffered: boolean = false;

  map: Map;
  leafletOptions = {
    center: [MapService.startLatitude, MapService.startLongitude],
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
 private basemap = 'https://maps{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpg';
// high def but jpg, so no background layer is possible
  // private basemap = 'https://maps{s}.wien.gv.at/basemap/geolandbasemap/normal/google3857/{z}/{y}/{x}.png';


  layers: Layer[] = [
    // tileLayer(this.worldMap, {
    //   attribution: 'World Map tiles by <a href='http://stamen.com'>Stamen Design</a>,
    // <a href='http://creativecommons.org/licenses/by/3.0'>CC BY 3.0</a> &mdash; Map data &copy;
    // <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors',
    //   subdomains: 'abcd',
    //   minZoom: 1,
    //   maxZoom: 16,
    // }),
    tileLayer(this.basemap, {
      minZoom: 1,
      maxZoom: 20,
      attribution: 'Data Source Austria: <a "href="https://www.basemap.at">basemap.at</a>',
      subdomains: ['', '1', '2', '3', '4'],
      bounds: [[46.35877, 8.782379], [49.037872, 17.189532]]
    })
  ];

  constructor(
    private locationService: LocationService,
    private mapService: MapService,
    private sidebarService: SidebarService,
    private filterService: FilterService,
    public authService: AuthService) {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subs?.unsubscribe();
  }

  onMapReady(map: Map) {
    control.scale({position: 'bottomleft', metric: true, imperial: false}).addTo(map);

    this.map = map;
    this.mapService.map = map;

    this.filter = this.filterLocationFromMapView();
    this.filter.radiusBuffered = true;

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

    this.subs.add(this.filterService.filterLocationObservable.subscribe(filterLocation => {
      this.onFilterChanged(filterLocation);
    }));

    this.map.on('moveend', () => { this.onMapMoved(); });

    setTimeout(() => this.map.invalidateSize({pan: false}));
  }

  private onMapMoved() {
    if (this.filter.radiusEnabled) {
      return;
    }
    const newFilter = this.filterLocationFromMapView();

    if (!this.isNewFilterWithinCurrentFilter(newFilter)) {
      const oldCategoryId = this.filter.categoryId;
      this.filter = newFilter;
      newFilter.categoryId = oldCategoryId;
      this.isFilterBuffered = false;
      this.getLocationsAndConvertToLayerGroup();
    }
  }

  private onFilterChanged(change: FilterLocation): void {
    let drawCircles = false;
    let reloadRequired = false;

    if (this.filter.hashtag !== change.hashtag) {
      this.filter.hashtag = change.hashtag;
      reloadRequired = true;
    }
    if (this.filter.categoryId !== change.categoryId) {
      this.filter.categoryId = change.categoryId;
      reloadRequired = true;
    }
    if (change.radiusEnabled) {
      drawCircles = true;
      if (this.filter.radiusEnabled
        && !reloadRequired
        && this.filter.radius === change.radius
        && this.filter.coordinates.distanceTo(this.map.getCenter()) < 20) {
        return;
      }

      reloadRequired = true;
      this.filter.radiusEnabled = true;
      this.filter.radius = change.radius;
      this.filter.coordinates = this.map.getCenter();
    } else if (this.filter.radiusEnabled) {
      reloadRequired = true;
      this.filter.radiusEnabled = true;
      this.filter.radius = this.filterLocationFromMapView().radius;
    } else { // radiusEnabled was disabled and is still disabled
      const filterFromMapV = this.filterLocationFromMapView();
      reloadRequired = reloadRequired || !this.isNewFilterWithinCurrentFilter(filterFromMapV);
    }

    if (reloadRequired) {
      console.log('reload of locations');
      this.removeCircle();
      if (drawCircles) {
        this.circle = new Circle(this.map.getCenter(), this.filter.radius).addTo(this.map);
      }
      this.getLocationsAndConvertToLayerGroup();
    } else {
      console.log('no reload of locations required');
    }
  }

  /**
   * Get a filter location cover the entire map
   * @returns FilterLocation with the map center and the radius to cover the displayed map
   */
  private filterLocationFromMapView(): FilterLocation {
    const center: LatLng = this.map.getCenter();
    const northEast: LatLng = this.map.getBounds().getNorthEast();
    const radius = center.distanceTo(northEast);

    return {
      coordinates: center,
      radius: radius,
      radiusEnabled: false,
      radiusBuffered: false,
    };
  }

  private getLocationsAndConvertToLayerGroup() {
    if (!this.filter.radiusEnabled) {
      this.addBufferToFilterRadius();
    }
    this.subs.add(this.locationService.filterLocation(this.filter).subscribe(
      (result: MLocation[]) => {
        this.locationList = result;
        this.addMarkers();
        console.log(result);
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

  private removeCircle(): void {
    if (this.circle != null) {
      this.map.removeLayer(this.circle);
    }
  }

  public removeMLocation(id: number) {
    const found = this.locationList.find(ele => ele.id === id);
    if (found != null) {
      this.locMarkerGroup?.removeLayer(found);
    }
  }

  private addBufferToFilterRadius(): void {
    if (!this.isFilterBuffered) {
      const center: LatLng = this.map.getCenter();
      const northEast: LatLng = this.map.getBounds().getNorthEast();
      const buffer = center.distanceTo(northEast) / 2;
      this.filter.radius += buffer;
      this.isFilterBuffered = true;
    }
  }

  private isNewFilterWithinCurrentFilter(newFilter: FilterLocation): boolean {
    const distance = this.filter.coordinates.distanceTo(newFilter.coordinates) + newFilter.radius;
    const result = distance < this.filter.radius;
    return result;
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
