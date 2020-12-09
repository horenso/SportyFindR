/// <reference types='leaflet-sidebar-v2' />
import { Component, OnInit, Input } from '@angular/core';
import {Map, SidebarOptions} from 'leaflet';

@Component({
  selector: 'app-map-sidebar',
  templateUrl: './map-sidebar.component.html',
  styleUrls: ['./map-sidebar.component.scss']
})
export class MapSidebarComponent implements OnInit {

  @Input() map: Map;
  @Input() options: SidebarOptions;

  constructor() { }

  ngOnInit(): void {
  }

}
