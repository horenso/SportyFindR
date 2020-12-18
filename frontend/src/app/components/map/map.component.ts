import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {control, Layer, LayerGroup, Map, Marker, tileLayer} from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {Location} from '../../dtos/location';
import {MarkerLocation} from '../../util/marker-location';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent {

  @Output() locationClicked = new EventEmitter();

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

  private locationList: Location[];
  private locMarkerGroup: LayerGroup<MarkerLocation>;
  private locLayerGroup: LayerGroup<Marker> = new LayerGroup<Marker>();
  private worldMap = 'https://stamen-tiles-{s}.a.ssl.fastly.net/watercolor/{z}/{x}/{y}.jpg';
//  private basemap = 'https://maps{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpg';
// high def but jpg, so no background layer is possible
  private basemap = 'https://maps{s}.wien.gv.at/basemap/geolandbasemap/normal/google3857/{z}/{y}/{x}.png';
// private basemap = 'https://maps{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpg';
// high def but jpg, so no background layer is possible

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

  markerLayerGroup: LayerGroup = new LayerGroup();

  constructor(private locationService: LocationService) {}

  onMapReady(map: Map) {
    control.scale({position: 'bottomleft', metric: true, imperial: false}).addTo(map);

    this.map = map;
    
    this.getLocationsAndConvertToLayerGroup();
  }

  private getLocationsAndConvertToLayerGroup() {
    this.locationService.getAllLocations().subscribe(
      result => {
        this.locationList = result;
        this.convertLocations();
      },
      error => {
        console.log('Error retrieving locations from backend: ' + error);
      }
    );
  }

  private convertLocations() {
    this.locMarkerGroup = new LayerGroup<MarkerLocation>();
    this.locationList.forEach(location => {
      const markerLocation = new MarkerLocation(location);
      this.locMarkerGroup.addLayer(markerLocation.on('click', () => {
        this.onMarkerClick(markerLocation);
      }));
      this.locMarkerGroup.addTo(this.locLayerGroup);
      this.locLayerGroup.addTo(this.map);
    });
  }

  private onMarkerClick(mLoc: MarkerLocation) {
    console.log(mLoc.id);
    this.locationClicked.emit(mLoc.id);
  }
}