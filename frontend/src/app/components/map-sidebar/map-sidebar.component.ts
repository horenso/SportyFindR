import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {SidebarActionService, SidebarActionType} from '../../services/sidebar-action.service';
import {Subscription} from 'rxjs';
import {ViewSpotsComponent} from '../view-spots/view-spots.component';

@Component({
  selector: 'app-map-sidebar',
  templateUrl: './map-sidebar.component.html',
  styleUrls: ['./map-sidebar.component.scss']
})
export class MapSidebarComponent implements OnInit, OnDestroy {

  actionTypeEnum = SidebarActionType;

  active: boolean = false;
  @Output() sidebarActive = new EventEmitter<boolean>();
  actionType: SidebarActionType;
  private subscription: Subscription;

  toggleActive() {
    this.active = !this.active;
    this.emitActive();
  }

  private emitActive() {
    this.sidebarActive.emit(this.active);
  }

  private getSidebarAction() {
    this.subscription = this.actionService.action$.subscribe(
      actionType => {
        this.actionType = actionType;
        this.doAction();
      },
      error => {
        console.log('Sidebar lost communication to main application. Error: ', + error);
      },
    );
  }

  private doAction() {
    switch(this.actionType) {
      case SidebarActionType.NoAction:
      case SidebarActionType.Success:
      case SidebarActionType.Cancelled:
      case SidebarActionType.Failed:
        this.active = false;
        break;
      case SidebarActionType.CreateLocSpot:
      case SidebarActionType.ShowSpotsLoc:
      case SidebarActionType.ShowMessages:
        this.active = true;
        break;
    }
    this.emitActive();
  }

  constructor(private actionService: SidebarActionService) {
  }

  ngOnInit(): void {
    this.getSidebarAction();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
