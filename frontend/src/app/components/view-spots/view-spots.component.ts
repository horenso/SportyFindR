import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {Spot} from '../../dtos/spot';
import {SpotService} from 'src/app/services/spot.service';
import {SidebarActionType, SidebarService} from 'src/app/services/sidebar.service';
import {ActivatedRoute, Router} from '@angular/router';
import {parseIntStrictly} from './../../util/parse-int';

@Component({
  selector: 'app-view-spots',
  templateUrl: './view-spots.component.html',
  styleUrls: ['./view-spots.component.scss']
})
export class ViewSpotsComponent implements OnInit {

  locationIdString: string;
  locationId: number = null;

  @Output() selectSpot = new EventEmitter();

  public spots: Spot[] = [];

  constructor(
    private spotService: SpotService,
    private sidebarService: SidebarService,
    private route: Router,
    private activeRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.activeRoute.params.subscribe(params => {
      this.locationIdString = params.locId;
      this.locationId = parseInt(this.locationIdString);

      console.log(this.locationId);
      console.log(this.locationIdString);

      this.locationId = parseIntStrictly(this.locationIdString);

      if (isNaN(this.locationId)) {
        console.log('it is not a number!');
      } else {
        console.log('Correct: ' + this.locationId);
        this.spotService.getSpotsByLocation(this.locationId).subscribe(result => {
          this.spots = result;
        }, error => {
          console.log('could not find location!');
        });
      }
    });
  }

  onSelectedSpot(spot: Spot) {
    this.route.navigate(['locations', this.locationId, 'spots', spot.id]);
    this.selectSpot.emit(spot);
  }

  // private getSpots(): void {
  //   console.log(this.locationId);
  //   if (this.getSpotsSubscription != null) {
  //     this.getSpotsSubscription.unsubscribe();
  //   }
  //   this.getSpotsSubscription = this.spotService.getSpotsByLocation(this.locationId).subscribe(result => {
  //     this.spots = result;
  //     console.log(this.spots);
  //     this.changeDetectorRef.detectChanges();
  //   });
  //   console.log('Spots requested');
  // }

  // createSpot(): void {
  //   this.sidebarService.setAction(SidebarActionType.CreateSpot);
  //   this.changeDetectorRef.detectChanges();
  // }

  // ngOnDestroy() {
  //   this.getSpotsSubscription?.unsubscribe();
  //   this.locationClickedSubscription?.unsubscribe();
  // }
}
