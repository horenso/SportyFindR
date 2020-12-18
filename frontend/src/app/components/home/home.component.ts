import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {SidebarActionService} from '../../services/sidebar-action.service';

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
    private sidebarActionService: SidebarActionService,
    private changeDetectorRef: ChangeDetectorRef) {
  }

  onSidebarActive(sidebarActive: boolean) {
    this.sidebarActive = sidebarActive;
  }

  ngOnInit() {
  }

  createLocationWithSpot(): void {
  }

  onSelectedLoc(locationId: number) {
    this.selectedLocationId = locationId;
    this.changeDetectorRef.detectChanges();
  }
}
