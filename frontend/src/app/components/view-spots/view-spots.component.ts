import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {Spot} from '../../dtos/spot';
import {MapService} from '../../services/map.service';
import {Subscription} from 'rxjs';
import {SidebarActionService} from '../../services/sidebar-action.service';

@Component({
  selector: 'app-view-spots',
  templateUrl: './view-spots.component.html',
  styleUrls: ['./view-spots.component.scss']
})
export class ViewSpotsComponent implements OnInit, OnDestroy {
  spots: Spot[];
  private subscription: Subscription;
  constructor(private mapService: MapService, private cdr: ChangeDetectorRef, private sidebarActionService: SidebarActionService) { }

  ngOnInit(): void {
    this.setSpots();
  }
  getSpots(spots: Spot[]) {
    this.spots = spots;
  }
  private setSpots () {
    this.subscription = this.mapService.spots$.subscribe((spots: Spot[]) => {
        this.spots = spots;
        this.cdr.detectChanges();
        console.log(spots);
      },
      (error) => {
        console.log(error);
      }
    );
  }
  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
  createSpot() {
    this.sidebarActionService.setActionCreateSpot();
  }
  getMLoc() {
    this.ngOnInit();
    return this.spots[0].markerLocation;
  }
}
