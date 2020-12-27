import {ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {SidebarActionType, SidebarService} from '../../services/sidebar.service';
import {Subscription} from 'rxjs';
import {MapService} from 'src/app/services/map.service';
import {Spot} from 'src/app/dtos/spot';

@Component({
  selector: 'app-map-sidebar',
  templateUrl: './map-sidebar.component.html',
  styleUrls: ['./map-sidebar.component.scss']
})
export class MapSidebarComponent implements OnInit, OnDestroy, OnChanges {

  @Input() locationId: number;
  @Output() sidebarActive = new EventEmitter<boolean>();

  actionTypeEnum = SidebarActionType;
  visible: boolean = false;
  actionType: SidebarActionType = SidebarActionType.NoAction;

  private actionSubscription: Subscription;
  private clickedLocationSubscription: Subscription;

  currentSpot: Spot = null;

  constructor(
    private sidebarService: SidebarService,
    private mapService: MapService,
    private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.getSidebarAction();
    this.listenToClickedLocations();
  }

  ngOnChanges(changes: SimpleChanges): void {
      console.log('Changes in MapSidebarComponent: ' + JSON.stringify(changes))
      if (this.locationId == null) {
        return;
      }
      if (this.actionType === SidebarActionType.NoAction) {
        this.actionType = SidebarActionType.ShowSpotsLoc;
      }
      this.visible = true;
  }

  onSelectSpot(spot: Spot) {
    this.currentSpot = spot;
    this.actionType = SidebarActionType.ShowMessages;
    this.changeDetectorRef.detectChanges();
  }

  toggleActive() {
    this.visible = !this.visible;
    this.emitActive();
  }

  ngOnDestroy(): void {
    this.actionSubscription.unsubscribe();
  }

  private emitActive() {
    this.sidebarActive.emit(this.visible);
  }

  private getSidebarAction(): void {
    this.actionSubscription = this.sidebarService.action$.subscribe(
      actionType => {
        this.actionType = actionType;
        if (actionType === SidebarActionType.ShowMessages) {
          this.locationId = this.sidebarService.location.id;
        }
        this.doAction();
      },
      error => {
        console.log('Sidebar lost communication to main application. Error: ', +error);
      },
    );
  }

  private listenToClickedLocations(): void {
    this.clickedLocationSubscription = this.mapService.locationClickedObservable.subscribe(result => {
      this.sidebarService.location = result.changeToLocation();
      this.actionType = SidebarActionType.ShowSpotsLoc;
      this.doAction();
      this.changeDetectorRef.detectChanges();
    });
  }

  onGoBackFromMessages(): void {
    this.actionType = SidebarActionType.ShowSpotsLoc;
    this.changeDetectorRef.detectChanges();
  }

  private doAction() {
    this.visible = (
      this.actionType === SidebarActionType.CreateLocSpot ||
      this.actionType === SidebarActionType.ShowSpotsLoc ||
      this.actionType === SidebarActionType.ShowMessages
    );
    this.emitActive();
  }
}
