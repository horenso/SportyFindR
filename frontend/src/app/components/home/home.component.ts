import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {SidebarService} from '../../services/sidebar.service';
import {Router} from '@angular/router';
import {MLocation} from '../../util/m-location';
import {HashtagService} from '../../services/hashtag.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {

  selectedLocationId: number = null;

  sidebarActive: boolean = false;
  filterActive: boolean = true;

  constructor(
    public authService: AuthService,
    private sidebarService: SidebarService,
    private router: Router,
    private hashtagService: HashtagService) {
  }

  ngOnInit() {
    this.sidebarActive = !(this.router.routerState.snapshot.url.toString() === '/');
    if (this.sidebarActive) {
      this.sidebarService.setSidebarStateOpen();
    } else {
      this.sidebarService.setSidebarStateClosed();
    }

    this.sidebarService.changeVisibilityAndFocusObservable.subscribe(change => {
      this.sidebarActive = change.isVisible;
    });


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
    this.sidebarService.changeVisibilityAndFocus({isVisible: true});
    this.sidebarActive = true;
  }
}
