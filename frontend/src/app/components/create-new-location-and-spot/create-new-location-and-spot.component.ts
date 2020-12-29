import {Component, OnDestroy, OnInit} from '@angular/core';
import {Map, marker, Marker} from 'leaflet';
import {MapService} from '../../services/map.service';
import {SpotService} from '../../services/spot.service';
import {SidebarActionType, SidebarService} from '../../services/sidebar.service';
import {CategoryService} from '../../services/category.service';
import {MLocation} from '../../util/m-location';
import {MLocSpot} from '../../util/m-loc-spot';
import {Router} from '@angular/router';

@Component({
  selector: 'app-create-new-location-and-spot',
  templateUrl: './create-new-location-and-spot.component.html',
  styleUrls: ['./create-new-location-and-spot.component.scss']
})
export class CreateNewLocationAndSpotComponent implements OnInit, OnDestroy {

  locMarker: Marker;
  spot: MLocSpot;
  markerLocation: MLocation;
  private map: Map;

  constructor(
    private mapService: MapService,
    private spotService: SpotService,
    private categoryService: CategoryService,
    private sidebarService: SidebarService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.map = this.mapService.map;
    this.createMarker();
    this.markerLocation = this.sidebarService.markerLocation;
  }

  saveSpot(newSpot: MLocSpot) {
    console.log(newSpot);
    const newMarkerLocation = newSpot.markerLocation;
    newMarkerLocation.addTo(this.map);
    this.sidebarService.setAction(SidebarActionType.Success);
  }

  cancel() {
    this.sidebarService.setAction(SidebarActionType.Cancelled);
    this.router.navigate(['..']);
  }

  ngOnDestroy() {
    this.locMarker.removeFrom(this.map);
  }

  private createMarker() {
    this.locMarker = marker(this.map.getCenter(), {draggable: true});
    this.locMarker.addTo(this.map).on('click', () => {
    });
  }
}
