import {Component, OnDestroy, OnInit} from '@angular/core';
import {control, Layer, LayerGroup, Map, Marker, tileLayer} from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {Location} from '../../dtos/location';
import {MapService} from '../../services/map.service';
import {Subscription} from 'rxjs';
import {MarkerLocation} from '../../util/marker-location';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, OnDestroy {

  map: Map;

  private layerGroupSubscription: Subscription;
  private locMarkerSubscription: Subscription;

  private locLayerGroup: LayerGroup<Marker> = new LayerGroup<Marker>();

  private basemap = 'https://maps{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpg';
  locationList: Location[];

  layers: Layer[] = [
    tileLayer(this.basemap, {
      minZoom: 1,
      maxZoom: 20,
      attribution: 'Data Source: <a href="https://www.basemap.at">basemap.at</a>',
      subdomains: ['', '1', '2', '3', '4'],
      bounds: [[46.35877, 8.782379], [49.037872, 17.189532]]
    })
  ];

  markerLayerGroup: LayerGroup = new LayerGroup();

  leafletOptions = {
    center: [48.208174, 16.37819],
    zoom: 13,
    zoomDelta: 0.5,
    wheelPxPerZoomLevel: 90,
    zoomSnap: 0,
    cursor: true
  };

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

  constructor(
    private locationService: LocationService,
    private mapService: MapService
  ) {
  }

  ngOnInit(): void {
  }


  ngOnDestroy(): void {
    this.layerGroupSubscription.unsubscribe();
    this.locMarkerSubscription.unsubscribe();
  }

  private addMarkers(): void {
    this.locationList.forEach((location: Location) => {
        const newMarker = new MarkerLocation(location);
        this.markerLayerGroup.addLayer(newMarker);
      }
    );
    this.layers.push(this.markerLayerGroup);
  }

}
