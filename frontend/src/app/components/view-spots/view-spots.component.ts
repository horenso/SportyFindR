import {ChangeDetectorRef, Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {Spot} from '../../dtos/spot';
import {SpotService} from 'src/app/services/spot.service';
import {MapService} from 'src/app/services/map.service';
import {SidebarActionType, SidebarService} from 'src/app/services/sidebar.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-view-spots',
  templateUrl: './view-spots.component.html',
  styleUrls: ['./view-spots.component.scss']
})
export class ViewSpotsComponent implements OnInit, OnDestroy {

  locationId: number = null;
  @Output() selectSpot = new EventEmitter();
  @Output() noSpot = new EventEmitter();

  private locationClickedSubscription: Subscription;
  private getSpotsSubscription: Subscription;

  public spots: Spot[] = [];

  constructor(
    private spotService: SpotService,
    private mapService: MapService,
    private changeDetectorRef: ChangeDetectorRef,
    private sidebarService: SidebarService
  ) {
  }

  ngOnInit(): void {
    this.locationId = this.sidebarService.location.id;
    this.locationClickedSubscription = this.mapService.locationClickedObservable.subscribe(result => {
      this.locationId = this.sidebarService.location.id;
      this.getSpots();
    });
    this.getSpots();
  }

  onSelectedSpot(spot: Spot) {
    this.sidebarService.spot = spot;
    this.selectSpot.emit(spot);
  }

  private getSpots(): void {
    console.log(this.locationId);
    if (this.getSpotsSubscription != null) {
      this.getSpotsSubscription.unsubscribe();
    }
    this.getSpotsSubscription = this.spotService.getSpotsByLocation(this.locationId).subscribe(result => {
      this.spots = result;
      console.log(this.spots);
      this.changeDetectorRef.detectChanges();
    });
    console.log('Spots requested');
  }

  createSpot(): void {
    this.sidebarService.setAction(SidebarActionType.CreateSpot);
    this.changeDetectorRef.detectChanges();
  }

  ngOnDestroy() {
    this.getSpotsSubscription?.unsubscribe();
    this.locationClickedSubscription?.unsubscribe();
  }
}
