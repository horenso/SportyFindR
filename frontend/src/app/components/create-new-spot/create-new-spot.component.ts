import {Component, OnInit} from '@angular/core';
import {MapService} from '../../services/map.service';
import {SpotService} from '../../services/spot.service';
import {CategoryService} from '../../services/category.service';
import {SidebarActionType, SidebarService} from '../../services/sidebar.service';
import {Location} from '../../dtos/location';
import {Spot} from '../../dtos/spot';
import {MarkerLocation} from '../../util/marker-location';
import {Map} from 'leaflet';


@Component({
  selector: 'app-create-new-spot',
  templateUrl: './create-new-spot.component.html',
  styleUrls: ['./create-new-spot.component.scss']
})
export class CreateNewSpotComponent implements OnInit {

  markerLocation: MarkerLocation;
  private map: Map;
  constructor(
    private mapService: MapService,
    private spotService: SpotService,
    private categoryService: CategoryService,
    private sidebarService: SidebarService) {
  }

  ngOnInit(): void {
    this.markerLocation = this.viewSpotsComponent.getMLoc();
  }

  saveSpot(newSpot: Spot) {
    console.log(newSpot);
    this.sidebarService.setAction(SidebarActionType.Success);
  }
  cancel() {
    this.sidebarActionService.setActionCancelled();
  }
}
