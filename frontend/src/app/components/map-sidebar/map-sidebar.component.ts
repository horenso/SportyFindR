import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {SidebarActionService, SidebarActionType} from '../../services/sidebar-action.service';
import {Subscription} from 'rxjs';

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

  constructor(private actionService: SidebarActionService) {
  }

  toggleActive() {
    this.active = !this.active;
    this.emitActive();
  }

  ngOnInit(): void {
    this.getSidebarAction();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
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
        console.log('Sidebar lost communication to main application. Error: ', +error);
      },
    );
  }

  private doAction() {
//    console.log('Current action is ' + this.actionType + '.');
    if (this.actionType === SidebarActionType.NoAction ||
      this.actionType === SidebarActionType.Success ||
      this.actionType === SidebarActionType.Cancelled ||
      this.actionType === SidebarActionType.Failed
    ) {
      this.active = false;
    } else if (this.actionType === SidebarActionType.CreateLocSpot) {
      // do stuff in the template
      this.active = true;
    } else {
      // This should not happen
      console.log('SidebarActionType is ' + this.actionType);
    }
    this.emitActive();
  }
}
