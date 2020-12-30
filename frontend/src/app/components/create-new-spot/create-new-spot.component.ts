import {Component, OnInit} from '@angular/core';
import {SidebarService} from '../../services/sidebar.service';
import {MLocation} from '../../util/m-location';
import {MLocSpot} from '../../util/m-loc-spot';
import {ActivatedRoute, Router} from '@angular/router';
import {parseIntStrictly} from 'src/app/util/parse-int';
import {LocationService} from 'src/app/services/location.service';


@Component({
  selector: 'app-create-new-spot',
  templateUrl: './create-new-spot.component.html',
  styleUrls: ['./create-new-spot.component.scss']
})
export class CreateNewSpotComponent implements OnInit {

  public locationId: number;
  public mLocation: MLocation;

  constructor(
    private sidebarService: SidebarService,
    private locationService: LocationService,
    private router: Router,
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
      }

      if (this.sidebarService.markerLocation != null && this.sidebarService.markerLocation.id === this.locationId) {
        this.mLocation = this.sidebarService.markerLocation;
        console.log('hi');
      } else {
        this.locationService.getLocationById(this.locationId).subscribe(result => {
          this.mLocation = result;
          this.sidebarService.markerLocation = result;
        });
      }
    });
  }

  saveSpot(newSpot: MLocSpot) {
    console.log('new Spot: ' + newSpot);
    this.router.navigate(['locations', this.locationId]);
  }

  cancel() {
    this.router.navigate(['locations', this.locationId]);
  }
}
