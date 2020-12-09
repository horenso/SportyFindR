import {Component, OnInit, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'app-map-sidebar',
  templateUrl: './map-sidebar.component.html',
  styleUrls: ['./map-sidebar.component.scss']
})
export class MapSidebarComponent implements OnInit {

  active: boolean = false;
  @Output() sidebarActive = new EventEmitter<boolean>();

  toggleActive() {
    this.active = !this.active;
    this.sidebarActive.emit(this.active);
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
