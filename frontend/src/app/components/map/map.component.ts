import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {control, icon, Layer, LayerGroup, Map, marker, Marker, tileLayer} from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {MapService} from 'src/app/services/map.service';
import { SidebarService } from 'src/app/services/sidebar.service';
import {MLocation} from '../../util/m-location';

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

    this.sidebarService.visibilityChanged$.subscribe(changed => {
      setTimeout(() => {
        this.map.invalidateSize({pan: false});
      }, 300);
    });
  }

  ngOnInit(): void {
    const iconRetinaUrl = 'assets/marker-icon-2x.png';
    const iconUrl = 'assets/marker-icon.png';
    const shadowUrl = 'assets/marker-shadow.png';
    const iconDefault = icon({
      iconRetinaUrl,
      iconUrl,
      shadowUrl,
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      tooltipAnchor: [16, -28],
      shadowSize: [41, 41]
    });
    Marker.prototype.options.icon = iconDefault;
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
}
