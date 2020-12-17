import {Component, OnDestroy, OnInit} from '@angular/core';
import {control, Layer, LayerGroup, Map, Marker, tileLayer} from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {Location} from '../../dtos/location';
import {MapService} from '../../services/map.service';
import {MarkerLocation} from '../../util/marker-location';
import {Spot} from '../../dtos/spot';
import {SpotService} from '../../services/spot.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, OnDestroy {

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
  private spots: Spot[];
  private layerGroupSubscription: Subscription;
  private locMarkerSubscription: Subscription;
  private locationList: Location[];
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

  constructor(
    private locationService: LocationService,
    private mapService: MapService,
    private spotService: SpotService
  ) {
  }

  ngOnInit(): void {
  }

  onMapReady(map: Map) {
    control.scale({position: 'bottomleft', metric: true, imperial: false}).addTo(map);

    this.locationService.getAllLocations().subscribe((result: Location[]) => {
        this.locationList = result;
        console.log(this.locationList);
        this.addMarkers();
      }
    );

    this.map = map;
    this.mapService.setMap(this.map);

    this.mapService.initLayers();
    this.initLocMarkers();
  }

  private initLocMarkers() {
    this.layerGroupSubscription = this.mapService.locationLayerGroup$.subscribe(
      locationLayerGroup => {
        this.locLayerGroup = locationLayerGroup;
        if (this.map.hasLayer(this.locLayerGroup)) {
          this.locLayerGroup.removeFrom(this.map);
        }
        this.locLayerGroup.addTo(this.map);
        this.subscribeLocMarkers();
      },
      error => {
        console.log('Error receiving Location LayerGroup. Error: ' + error);
      }
    );
  }

  private subscribeLocMarkers() {
    this.mapService.locMarker$.subscribe(
      locMarker => {
        this.locLayerGroup.addLayer(locMarker);
      },
      error => {
        console.log('Error waiting for Location Markers' + error);
      }
    );
  }

  private addMarkers(): void {
    this.locationList.forEach((location: Location) => {
        const newMarker = new MarkerLocation(location);
        this.markerLayerGroup.addLayer(newMarker);
      }
    );
    this.layers.push(this.markerLayerGroup);
  }

  ngOnDestroy(): void {
    if (this.layerGroupSubscription) {
      this.layerGroupSubscription.unsubscribe();
    }
    if (this.locMarkerSubscription) {
      this.locMarkerSubscription.unsubscribe();
    }
  }
}
