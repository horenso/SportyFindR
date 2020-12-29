import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {SidebarActionType, SidebarService} from '../../services/sidebar.service';
import {Location} from '../../dtos/location';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { Router } from '@angular/router';

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
    private router: Router) {
  }

  onSidebarActive(sidebarActive: boolean) {
    this.sidebarActive = sidebarActive;
  }

  ngOnInit() {
  }

  onSelectedLoc(location: Location): void {
    this.selectedLocationId = location.id;
    this.sidebarService.setAction(SidebarActionType.ShowSpotsLoc);
    this.sidebarService.location = location;

  }

  createLocationWithSpot() {
    this.router.navigate(['locations', 'new']);
    this.sidebarService.setAction(SidebarActionType.CreateLocSpot);
  }
}
