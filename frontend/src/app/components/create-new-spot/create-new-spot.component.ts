import { Component, OnInit } from '@angular/core';
import {MapService} from '../../services/map.service';
import {SpotService} from '../../services/spot.service';
import {CategoryService} from '../../services/category.service';
import {SidebarActionService} from '../../services/sidebar-action.service';
import {Subscription} from 'rxjs';
import {Category} from '../../dtos/category';
import {Spot} from '../../dtos/spot';
import {ViewSpotsComponent} from '../view-spots/view-spots.component';
import {MarkerLocation} from '../../util/marker-location';


@Component({
  selector: 'app-create-new-spot',
  templateUrl: './create-new-spot.component.html',
  styleUrls: ['./create-new-spot.component.scss']
})
export class CreateNewSpotComponent implements OnInit {
  private categorySubscription: Subscription;
  categories: Category[];
  spot: Spot = new Spot (null, '', '', null, null );
  id: number;
  markerLocation: MarkerLocation;
  constructor(
    private mapService: MapService,
    private spotService: SpotService,
    private categoryService: CategoryService,
    private sidebarActionService: SidebarActionService,
    private viewSpotsComponent: ViewSpotsComponent
  ) {
  }

  ngOnInit(): void {
    this.markerLocation = this.viewSpotsComponent.getMLoc();
    this.spot = new Spot(null, '', '', null, this.markerLocation );
    this.getCategories();
  }
  private getCategories() {
    this.categorySubscription = this.categoryService.getAllCategories().subscribe(
      categories => {
        this.categories = categories;
      }
    );
  }
  saveSpot() {
    console.log('Creating new spot: ', this.spot);
    this.spotService.createSpot(this.spot).subscribe(
      newSpot => {
        console.log(newSpot);
        // Add to Location list
        this.mapService.addMarkerToLocations(newSpot.markerLocation);
        this.sidebarActionService.setActionSuccess();
      },
      error => {
        console.log('Failed to store new spot in the backend. Error: ' + error);
        this.sidebarActionService.setActionFailed();
      }
    );
  }
  cancel() {
    this.sidebarActionService.setActionCancelled();
  }
}
