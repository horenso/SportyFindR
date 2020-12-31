import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {control, icon, Layer, LayerGroup, Map, marker, Marker, tileLayer} from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {MapService} from 'src/app/services/map.service';
import { SidebarService, VisibilityFocusChange } from 'src/app/services/sidebar.service';
import {IconType, MLocation} from '../../util/m-location';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {

  @Output() mLocClicked = new EventEmitter();

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
  private locMarkerGroup: LayerGroup<MLocation>;

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

  private newMarkerSubscription;

  constructor(
    private locationService: LocationService,
    private mapService: MapService,
    private sidebarService: SidebarService) {
  }

  onMapReady(map: Map) {
    control.scale({position: 'bottomleft', metric: true, imperial: false}).addTo(map);

    this.map = map;
    this.mapService.map = map;

    this.getLocationsAndConvertToLayerGroup();
    this.newMarkerSubscription = this.mapService.addMarkerObservable.subscribe(markerLocation => {
      this.locationList.push(markerLocation);
      this.locMarkerGroup.addLayer(markerLocation);
    });

    this.mapService.removeMarkerLocObservable.subscribe(idToRemove => {
      this.removeMLocation(idToRemove);
    });

    this.mapService.resetAllMarkerIconsObservable.subscribe(() => {
      console.log('reset all marker icons');
      this.locationList.forEach(loc => {
        console.log('resetting icon for loc : ' + loc.id);
        loc.changeIcon(IconType.Default);
      })
    })

    this.sidebarService.changeVisibilityAndFocusObservable.subscribe(change => {
      this.changeVisibilityAndFocus(change);
    });
  }

  ngOnInit(): void {
  }

  private getLocationsAndConvertToLayerGroup() {
    this.locationService.getAllMarkerLocations().subscribe(
      (result: MLocation[]) => {
        this.locationList = result;
        this.addMarkers();
      },
      error => {
        console.log('Error retrieving locations from backend: ', error);
      }
    );
  }

  private addMarkers(): void {
    this.locMarkerGroup = new LayerGroup<MLocation>();
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
    if (this.sidebarService.previousVisibility === change.isVisible && change.locationInFocus !== null) {
      this.map.setView(change.locationInFocus.getLatLng(), this.map.getZoom());
    }
    if (this.sidebarService.previousVisibility !== change.isVisible) {
      setTimeout(() => {
        console.log('resizing map')
        this.map.invalidateSize({pan: false});

        if (change.locationInFocus !== null) {
          this.map.setView(change.locationInFocus.getLatLng(), this.map.getZoom());
        }
      }, 300);
    }
  }
}
