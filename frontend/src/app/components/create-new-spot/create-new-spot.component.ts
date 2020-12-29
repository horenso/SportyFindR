import {Component, OnInit} from '@angular/core';
import {MapService} from '../../services/map.service';
import {SpotService} from '../../services/spot.service';
import {CategoryService} from '../../services/category.service';
import {SidebarActionType, SidebarService} from '../../services/sidebar.service';
import {MLocation} from '../../util/m-location';
import {Map} from 'leaflet';
import {MLocSpot} from "../../util/m-loc-spot";


@Component({
  selector: 'app-create-new-spot',
  templateUrl: './create-new-spot.component.html',
  styleUrls: ['./create-new-spot.component.scss']
})
export class CreateNewSpotComponent implements OnInit {

  markerLocation: MLocation;
  constructor(
    private mapService: MapService,
    private spotService: SpotService,
    private categoryService: CategoryService,
    private sidebarService: SidebarService) {
  }

  ngOnInit(): void {
    this.markerLocation = this.sidebarService.markerLocation;
  }

  saveSpot(newSpot: MLocSpot) {
    console.log(newSpot);
    this.sidebarService.setAction(SidebarActionType.Success);
  }
  cancel() {
    this.sidebarService.setAction(SidebarActionType.Cancelled);
  }
}
