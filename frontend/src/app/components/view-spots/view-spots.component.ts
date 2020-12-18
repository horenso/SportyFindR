import {ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {Spot} from '../../dtos/spot';
import {Subscription} from 'rxjs';
import { SpotService } from 'src/app/services/spot.service';
import { MapService } from 'src/app/services/map.service';
import { result } from 'lodash';

@Component({
  selector: 'app-view-spots',
  templateUrl: './view-spots.component.html',
  styleUrls: ['./view-spots.component.scss']
})
export class ViewSpotsComponent implements OnInit, OnChanges, OnDestroy {

  locationId: number;
  @Output() selectSpot = new EventEmitter();

  public spots: Spot[] = [];
  private subscription: Subscription;
  active: boolean = false;

  constructor(
    private spotService: SpotService,
    private mapService: MapService,
    private changeDetectorRef: ChangeDetectorRef
    ) { }

  ngOnInit(): void {
    this.mapService.markerClicked$.subscribe(result => this.locationId = result);
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log('Changes in ViewSpotsComponent: ' + JSON.stringify(changes))
    if (!changes.locationId.firstChange && changes.locationId && this.locationId != null) {
      this.changeDetectorRef.detectChanges();
      this.getSpots();
      this.active = true;
      console.log('In spot view: ' + this.locationId);
    }
  }

  onSelectedSpot(spot: Spot) {
    this.selectSpot.emit(spot);
  }
  
  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private getSpots(): void {
    this.spotService.getSpotsByLocation(this.locationId).subscribe(result => {
      this.spots = result;
    })
    console.log('Spots requested');
  }
}
