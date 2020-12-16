import {Component, OnDestroy, OnInit} from '@angular/core';
import {control, Layer, LayerGroup, Map, tileLayer} from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {Location} from '../../dtos/location';
import {MapService} from '../../services/map.service';
import {MarkerLocation} from '../../util/marker-location';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, OnDestroy {

  map: Map;

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
//        console.log(this.locationList);
        this.addMarkers();
      }
    );

    this.map = map;
    this.mapService.setMap(this.map);
  }

  constructor(
    private locationService: LocationService,
    private mapService: MapService
  ) {
  }

  ngOnInit(): void {
  }


  ngOnDestroy(): void {
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
