import {Component, Input, OnInit} from '@angular/core';
import {SidebarService} from '../../services/sidebar.service';

@Component({
  selector: 'app-map-sidebar',
  templateUrl: './map-sidebar.component.html',
  styleUrls: ['./map-sidebar.component.scss']
})
export class MapSidebarComponent implements OnInit {

  @Input() visibleStart: boolean = true;
  public visible: boolean = true;

  constructor(private sidebarService: SidebarService) {
  }

  ngOnInit(): void {
    this.visible = this.visibleStart;
    this.sidebarService.changeVisibilityAndFocusObservable.subscribe(change => {
      this.visible = change.isVisible;
    });
  }
}
