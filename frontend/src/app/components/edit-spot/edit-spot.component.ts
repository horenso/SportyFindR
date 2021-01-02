import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { SidebarService } from 'src/app/services/sidebar.service';
import { SpotService } from 'src/app/services/spot.service';
import { MLocSpot } from 'src/app/util/m-loc-spot';
import { parseIntStrictly } from 'src/app/util/parse-int';

@Component({
  selector: 'app-edit-spot',
  templateUrl: './edit-spot.component.html',
  styleUrls: ['./edit-spot.component.scss']
})
export class EditSpotComponent implements OnInit {

  spot: MLocSpot = null;

  constructor(
    private activedRoute: ActivatedRoute,
    private sidebarService: SidebarService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private spotService: SpotService
    ) { }

  ngOnInit(): void {
    if (this.sidebarService.spot != null) {
      this.spot = this.sidebarService.spot;
    } else {
      let spotId: number;
      this.activedRoute.params.subscribe(params => {
        spotId = parseIntStrictly(params.spotId);
        if (isNaN(spotId)) {
          console.log('it is not a number!');
          return;
        }
          console.log('Correct: ' + spotId);
          this.spotService.getSpotById(spotId).subscribe(result => {
            this.spot = result;
          });
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }

  updateSpot(spot: MLocSpot): void {
    console.log('update spot: ' + JSON.stringify(spot));
    this.spotService.updateSpot(spot).subscribe(result => {
      console.log('Spot updated!');
      this.sidebarService.spot = result;
      this.router.navigate(['../'], { relativeTo: this.activatedRoute });
    });
  }
}
