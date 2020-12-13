import {Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {SidebarActionService} from '../../services/sidebar-action.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  sidebarActive: boolean = false;

  constructor(public authService: AuthService, private sidebarActionService: SidebarActionService) { }

  onSidebarActive(sidebarActive: boolean) {
    this.sidebarActive = sidebarActive;
  }

  ngOnInit() {
  }

  createLocationWithSpot() {
    this.sidebarActionService.setActionCreateLocSpot();
  }
}
