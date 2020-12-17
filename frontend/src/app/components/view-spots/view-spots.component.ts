import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {Spot} from '../../dtos/spot';
import {MapService} from '../../services/map.service';
import {Subscription} from 'rxjs';
import { SidebarActionService } from 'src/app/services/sidebar-action.service';

@Component({
  selector: 'app-view-spots',
  templateUrl: './view-spots.component.html',
  styleUrls: ['./view-spots.component.scss']
})
export class ViewSpotsComponent implements OnInit, OnDestroy {

  public spots: Spot[];
  private subscription: Subscription;

  constructor(
    private mapService: MapService, 
    private changeDetectorRef: ChangeDetectorRef,
    private sidebarActionService: SidebarActionService) { }

  ngOnInit(): void {
    this.setSpots();
    console.log(this.spots);
  }

  private setSpots () {
    this.subscription = this.mapService.spots$.subscribe((spots: Spot[]) => {
        this.spots = spots;
        console.log(spots);
        this.changeDetectorRef.detectChanges();
      },
      (error) => {
        console.log(error);
      }
    );
  }
  
  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
