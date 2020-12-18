import {ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {SidebarActionService, SidebarActionType} from '../../services/sidebar-action.service';
import {Subscription} from 'rxjs';
import {ViewSpotsComponent} from '../view-spots/view-spots.component';
import { MapService } from 'src/app/services/map.service';

@Component({
  selector: 'app-map-sidebar',
  templateUrl: './map-sidebar.component.html',
  styleUrls: ['./map-sidebar.component.scss']
})
export class MapSidebarComponent implements OnInit, OnDestroy, OnChanges {

  @Output() sidebarActive = new EventEmitter<boolean>();
  
  actionTypeEnum = SidebarActionType;
  visible: boolean = true;
  actionType: SidebarActionType = SidebarActionType.ShowSpotsLoc;
  private subscription: Subscription;

  currentLocationId: number;

  constructor(
    private actionService: SidebarActionService,
    private mapService: MapService,
    private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    // this.mapService.markerClicked$.subscribe(result => { 
    //   this.currentLocationId = result;
    //   console.log(this.currentLocationId);
    //   // this.changeDetectorRef.detectChanges();
    // });
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

  toggleActive() {
    this.visible = !this.visible;
    this.emitActive();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private emitActive() {
    this.sidebarActive.emit(this.visible);
  }

  private getSidebarAction() {
    this.subscription = this.actionService.action$.subscribe(
      actionType => {
        this.actionType = actionType;
        if (actionType === SidebarActionType.ShowMessages) {
          this.currentLocationId = this.actionService.currentLocId;
        }
        this.doAction();
      },
      error => {
        console.log('Sidebar lost communication to main application. Error: ', +error);
      },
    );
  }

  onGoBackFromMessages(): void {
    this.actionType = SidebarActionType.ShowSpotsLoc;
  }

  private doAction() {
    switch(this.actionType) {
      case SidebarActionType.NoAction:
      case SidebarActionType.Success:
      case SidebarActionType.Cancelled:
      case SidebarActionType.Failed:
        this.visible = false;
        break;
      case SidebarActionType.CreateLocSpot:
      case SidebarActionType.ShowSpotsLoc:
      case SidebarActionType.ShowMessages:
        this.visible = true;
        break;
    }
    this.emitActive();
  }
}
