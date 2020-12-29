import {ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {SidebarService} from '../../services/sidebar.service';
import {Subscription} from 'rxjs';
import {MapService} from 'src/app/services/map.service';
import {Spot} from 'src/app/dtos/spot';

@Component({
  selector: 'app-map-sidebar',
  templateUrl: './map-sidebar.component.html',
  styleUrls: ['./map-sidebar.component.scss']
})
export class MapSidebarComponent implements OnInit, OnDestroy {

  @Input() locationId: number;
  @Output() sidebarActive = new EventEmitter<boolean>();

  visible: boolean = true;
  currentSpot: Spot = null;
  private actionSubscription: Subscription;
  private clickedLocationSubscription: Subscription;

  constructor(
    private sidebarService: SidebarService,
    private mapService: MapService,
    private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    // this.getSidebarAction();
    // this.listenToClickedLocations();
  }

  onSelectSpot(spot: Spot) {
    this.currentSpot = spot;
    // this.actionType = SidebarActionType.ShowMessages;
    this.changeDetectorRef.detectChanges();
  }

  toggleActive() {
    this.visible = !this.visible;
    this.emitActive();
  }

  ngOnDestroy(): void {
    this.actionSubscription.unsubscribe();
  }

  onGoBackFromMessages(): void {
    // this.actionType = SidebarActionType.ShowSpotsLoc;
    this.changeDetectorRef.detectChanges();
  }

  private emitActive() {
    this.sidebarActive.emit(this.visible);
  }

  // private getSidebarAction(): void {
  //   this.actionSubscription = this.sidebarService.action$.subscribe(
  //     actionType => {
  //       this.actionType = actionType;
  //       if (actionType === SidebarActionType.ShowMessages) {
  //         this.locationId = this.sidebarService.markerLocation.id;
  //       }
  //       this.doAction();
  //     },
  //     error => {
  //       console.log('Sidebar lost communication to main application. Error: ', +error);
  //     },
  //   );
  // }

  // private listenToClickedLocations(): void {
  //   this.clickedLocationSubscription = this.mapService.locationClickedObservable.subscribe(result => {
  //     this.sidebarService.markerLocation = result;
  //     this.actionType = SidebarActionType.ShowSpotsLoc;
  //     this.doAction();
  //     this.changeDetectorRef.detectChanges();
  //   });
  // }

  // private doAction() {
  //   this.visible = (
  //     this.actionType === SidebarActionType.CreateLocSpot ||
  //     this.actionType === SidebarActionType.ShowSpotsLoc ||
  //     this.actionType === SidebarActionType.ShowMessages
  //   );
  //   this.emitActive();
  // }
}
