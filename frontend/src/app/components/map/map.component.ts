/// <reference types='leaflet-sidebar-v2' />
import {Component, OnDestroy, OnInit} from '@angular/core';
import {latLng, tileLayer, Map, control, marker, SidebarOptions} from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {Location} from '../../dtos/location';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, OnDestroy {

  public map: Map;

  private basemap = 'https://maps{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpg';

  public leafletOptions = {
    layers: [
      tileLayer(this.basemap, {
        minZoom: 1,
        maxZoom: 20,
        attribution: 'Data Source: <a href="https://www.basemap.at">basemap.at</a>',
        subdomains: ['', '1', '2', '3', '4'],
        bounds: [[46.35877, 8.782379], [49.037872, 17.189532]]
      })
    ],
    center: [48.208174, 16.37819],
    zoom: 13,
    zoomDelta: 0.5,
    wheelPxPerZoomLevel: 90,
    zoomSnap: 0,
    cursor: true
  };

  public sidebarOptions: SidebarOptions = {
    position: 'right',
    autopan: false,
    closeButton: true,
    container: 'sidebar',
  };

  onMapReady(map: Map) {
    control.scale({ position: 'bottomleft', metric: true, imperial: false }).addTo(map);

    this.map = map;
  }

  constructor(
    private locationService: LocationService
  ) {
  }

  ngOnInit(): void {
  }

/*
      this.locationService.getAllLocations().subscribe(
        (result: Location[]) => {
          this.locationList = result;
          console.log(this.locationList)
          this.addMarkers();
        }
      );
    }
  */
  ngOnDestroy(): void {
//    this.map.clearAllEventListeners();
//    this.map.remove();
  }

  private addMarkers(map: Map): void {
//    this.locationList.forEach(
//      (location: Location) => {
//        marker([location.latitude, location.longitude]).addTo(this.map);
//      }
//    );
  }

}
