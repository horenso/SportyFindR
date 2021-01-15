import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {SidebarService} from '../../services/sidebar.service';

@Component({
  selector: 'app-map-sidebar',
  templateUrl: './map-sidebar.component.html',
  styleUrls: ['./map-sidebar.component.scss']
})
export class MapSidebarComponent implements OnInit, OnDestroy {

  @Input() visibleStart: boolean = true;

  private subscription: Subscription;

  public visible: boolean = true;

  constructor(private sidebarService: SidebarService) {
  }

  ngOnInit(): void {
    this.visible = this.visibleStart;
    this.subscription = this.sidebarService.changeVisibilityAndFocusObservable.subscribe(change => {
      this.visible = change.isVisible;
    });
  }

  ngOnDestroy(): void {
    if (this.subscription != null) {
      this.subscription.unsubscribe();
    }
  }
}
