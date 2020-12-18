import {Component, OnInit} from '@angular/core';
import {MapService} from '../../services/map.service';
import {SpotService} from '../../services/spot.service';
import {CategoryService} from '../../services/category.service';
import {SidebarActionType, SidebarService} from '../../services/sidebar.service';
import {Location} from '../../dtos/location';


@Component({
  selector: 'app-create-new-spot',
  templateUrl: './create-new-spot.component.html',
  styleUrls: ['./create-new-spot.component.scss']
})
export class CreateNewSpotComponent implements OnInit {

  location: Location;

  constructor(
    private mapService: MapService,
    private spotService: SpotService,
    private categoryService: CategoryService,
    private sidebarService: SidebarService) {
  }

  ngOnInit(): void {
    this.location = this.sidebarService.location;
  }

  saveSpot() {
    this.sidebarService.setAction(SidebarActionType.Success);
  }

  cancel() {
    this.sidebarService.setAction(SidebarActionType.Cancelled);
  }
}
