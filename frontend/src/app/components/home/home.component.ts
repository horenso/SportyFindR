import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {SidebarActionType, SidebarService} from '../../services/sidebar.service';
import {MLocation} from "../../util/m-location";

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
    private changeDetectorRef: ChangeDetectorRef) {
  }

  onSidebarActive(sidebarActive: boolean) {
    this.sidebarActive = sidebarActive;
  }

  ngOnInit() {
  }

  onSelectedLoc(markerLocation: MLocation): void {
    this.selectedLocationId = markerLocation.id;
    this.sidebarService.setAction(SidebarActionType.ShowSpotsLoc);
    this.sidebarService.markerLocation = markerLocation;

  }

  createLocationWithSpot() {
    this.sidebarService.setAction(SidebarActionType.CreateLocSpot);
  }
}
