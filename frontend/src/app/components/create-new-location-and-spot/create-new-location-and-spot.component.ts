import {Component, OnDestroy, OnInit} from '@angular/core';
import {Map, marker, Marker} from 'leaflet';
import {MapService} from '../../services/map.service';
import {Subscription} from 'rxjs';
import {Spot} from '../../dtos/spot';
import {SpotService} from '../../services/spot.service';
import {Location} from '../../dtos/location';
import {SidebarActionService} from '../../services/sidebar-action.service';
import {Category} from '../../dtos/category';
import {CategoryService} from '../../services/category.service';
import {MarkerLocation} from '../../util/marker-location';

@Component({
  selector: 'app-create-new-location-and-spot',
  templateUrl: './create-new-location-and-spot.component.html',
  styleUrls: ['./create-new-location-and-spot.component.scss']
})
export class CreateNewLocationAndSpotComponent implements OnInit, OnDestroy {

  selectedCategory: Category;
  categories: Category[];
  locMarker: Marker;
  spot: Spot;
  private categorySubscription: Subscription;
  private mapSubscription: Subscription;
  private map: Map;

  constructor(
    private mapService: MapService,
    private spotService: SpotService,
    private categoryService: CategoryService,
    private sidebarActionService: SidebarActionService
  ) {
  }

  saveSpot() {
    this.spot.location = new Location(null, this.locMarker.getLatLng().lat, this.locMarker.getLatLng().lng);
    console.log('Creating new spot: ', this.spot);
    this.spotService.createSpot(this.spot).subscribe(
      newSpot => {
        console.log(newSpot);
        // Add to Location list

        const newMarkerLocation = new MarkerLocation(newSpot.location);
        this.mapService.addMarkerToLocations(newMarkerLocation);

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

  ngOnInit(): void {
    this.getMap();
    this.getCategories();
  }

  ngOnDestroy() {
    this.locMarker.removeFrom(this.map);
    this.mapSubscription.unsubscribe();
  }

  private getCategories() {
    this.categorySubscription = this.categoryService.getAllCategories().subscribe(
      categories => {
        this.categories = categories;
      }
    );
  }

  private getMap() {
    this.mapSubscription = this.mapService.map$.subscribe(
      map => {
        this.map = map;
        this.createMarker();
        this.initSpot();
      },
      error => {
        console.log('create-new-location-and-spot.component lost connection to map with error: ', +error);
      },
    );
  }

  private createMarker() {
    this.locMarker = marker(this.map.getCenter(), {draggable: true});
    this.locMarker.addTo(this.map);
  }

  private initSpot() {
    const location = new Location(null, null, null);
    this.spot = new Spot(null, '', '', null, location);
  }

}
