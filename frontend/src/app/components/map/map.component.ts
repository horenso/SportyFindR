import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import * as Leaflet from 'leaflet';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit, OnDestroy {
  private map;

  constructor() { }

  ngAfterViewInit(): void {
    this.map = Leaflet.map('map', {
      center: [ 48.208174, 16.37819 ],
      zoom: 3
    });
    const tiles = Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
});

tiles.addTo(this.map);
  }

  ngOnDestroy(): void {

  }

}
