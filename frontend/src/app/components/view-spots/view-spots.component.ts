import {Component, OnInit} from '@angular/core';
import {Spot} from '../../dtos/spot';
import {SpotService} from 'src/app/services/spot.service';
import {SidebarService} from 'src/app/services/sidebar.service';
import {Subscription} from 'rxjs';
import {MLocSpot} from '../../util/m-loc-spot';
import {ActivatedRoute, Router} from '@angular/router';
import {parseIntStrictly} from '../../util/parse-int';

@Component({
  selector: 'app-view-spots',
  templateUrl: './view-spots.component.html',
  styleUrls: ['./view-spots.component.scss']
})
export class ViewSpotsComponent implements OnInit {

  locationIdString: string;
  locationId: number = null;
  public spots: MLocSpot[] = [];
  private locationClickedSubscription: Subscription;
  private getSpotsSubscription: Subscription;

  constructor(
    private spotService: SpotService,
    private sidebarService: SidebarService,
    private route: Router,
    private activeRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.activeRoute.params.subscribe(params => {

      this.locationId = parseIntStrictly(params.locId);

      if (isNaN(this.locationId)) {
        console.log('it is not a number!');
      } else {
        console.log('Correct: ' + this.locationId);
        this.spotService.getSpotsByLocation(this.locationId).subscribe(
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


  onSelectedSpot(spot: Spot) {
    this.route.navigate(['locations', this.locationId, 'spots', spot.id]);
  }

  ngOnDestroy() {
    this.getSpotsSubscription?.unsubscribe();
    this.locationClickedSubscription?.unsubscribe();
  }
}
