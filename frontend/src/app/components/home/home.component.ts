import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {SidebarService} from '../../services/sidebar.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MLocation} from '../../util/m-location';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {

  selectedLocationId: number = null;

  sidebarActive: boolean = false;

  constructor(
    public authService: AuthService,
    private sidebarService: SidebarService,
    private router: Router,
    private activeRouter: ActivatedRoute) {
  }

  ngOnInit() {
    this.activeRouter.params.subscribe
  }

  onSidebarActive(sidebarActive: boolean) {
    this.sidebarActive = sidebarActive;
  }

  onSelectedLoc(markerLocation: MLocation): void {
    this.selectedLocationId = markerLocation.id;
    this.sidebarService.markerLocation = markerLocation;
  }

  createLocationWithSpot() {
    this.router.navigate(['locations', 'new']);
  }
}
