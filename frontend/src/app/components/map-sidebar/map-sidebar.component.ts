/// <reference types='leaflet-sidebar-v2' />
import {Component, Input, OnInit} from '@angular/core';
import {Map, SidebarOptions} from 'leaflet';

@Component({
  selector: 'app-map-sidebar',
  templateUrl: './map-sidebar.component.html',
  styleUrls: ['./map-sidebar.component.scss']
})
export class MapSidebarComponent implements OnInit {

  @Input() map: Map;

  public sidebarOptions: SidebarOptions = {
    position: 'right',
    autopan: false,
    closeButton: true,
    container: 'sidebar',
  };

  constructor() {
  }

  ngOnInit(): void {
  }

}
