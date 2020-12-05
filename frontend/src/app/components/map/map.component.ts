import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import * as Leaflet from 'leaflet';
import {LocationService} from 'src/app/services/location.service';
import {Location} from '../../dtos/location';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, AfterViewInit, OnDestroy {

  private map;
  locationList: Location[];

  constructor(
    private locationService: LocationService
  ) { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.map = Leaflet.map('map', {
      center: [ 48.208174, 16.37819 ],
      zoom: 13,
      zoomDelta: 0.5,
      wheelPxPerZoomLevel: 90,
      zoomSnap: 0,
      cursor: true
    });

    const tiles = Leaflet.tileLayer('https://maps{s}.wien.gv.at/basemap/bmaphidpi/{type}/google3857/{z}/{y}/{x}.{format}', {
      minZoom: 1,
      maxZoom: 20,
      attribution: 'Data Source: <a href="https://www.basemap.at">basemap.at</a>',
      subdomains: ['', '1', '2', '3', '4'],
      type: 'normal',
      format: 'jpg',
      bounds: [[46.35877, 8.782379], [49.037872, 17.189532]]
    });

    tiles.addTo(this.map);

    Leaflet.control.scale({
      metric: true,
      imperial: false
    }).addTo(this.map);

    this.locationService.getAllLocations().subscribe(
      (result: Location[]) => {
        this.locationList = result;
        console.log(this.locationList)
        this.addMarkers();
      }
    );
  }

  ngOnDestroy(): void {
    this.map.clearAllEventListeners();
    this.map.remove();
  }

  private addMarkers(): void {
    this.locationList.forEach(
      (location: Location) => {
        Leaflet.marker([location.latitude, location.longitude]).addTo(this.map);
      }
    );
  }

}
