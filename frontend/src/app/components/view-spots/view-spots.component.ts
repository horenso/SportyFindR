import {Component, OnDestroy, OnInit} from '@angular/core';
import {Spot} from '../../dtos/spot';
import {SpotService} from 'src/app/services/spot.service';
import {SidebarService} from 'src/app/services/sidebar.service';
import {Subscription} from 'rxjs';
import {MLocSpot} from '../../util/m-loc-spot';
import {ActivatedRoute, Router} from '@angular/router';
import {parseIntStrictly} from '../../util/parse-int';
import { IconType } from 'src/app/util/m-location';
import { Icon } from 'leaflet';

@Component({
  selector: 'app-view-spots',
  templateUrl: './view-spots.component.html',
  styleUrls: ['./view-spots.component.scss']
})
export class ViewSpotsComponent implements OnInit, OnDestroy {

  locationId: number = null;

  public spots: MLocSpot[] = [];

  private locationClickedSubscription: Subscription;
  private getSpotsSubscription: Subscription;

  constructor(
    private spotService: SpotService,
    private sidebarService: SidebarService,
    private route: Router,
    private activedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.activedRoute.params.subscribe(params => {

      this.locationId = parseIntStrictly(params.locId);

      if (isNaN(this.locationId)) {
        console.log('it is not a number!');
      } else {
        console.log('Correct: ' + this.locationId);
        this.spotService.getByLocationId(this.locationId).subscribe(
          result => {
            this.spots = result;
          },
          error => {
            console.log('could not find location!');
          }
        );
      }
    });
  }

  onClose(): void {
    this.route.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }

  onSelectedSpot(spot: Spot) {
    this.route.navigate(['locations', this.locationId, 'spots', spot.id]);
  }

  onCreateSpot() {
    this.route.navigate(['locations', this.locationId, 'spots', 'new']);
  }

  ngOnDestroy() {
    this.getSpotsSubscription?.unsubscribe();
    this.locationClickedSubscription?.unsubscribe();
  }
}
