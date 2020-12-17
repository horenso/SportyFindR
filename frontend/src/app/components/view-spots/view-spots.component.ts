import { Component, OnInit } from '@angular/core';
import {Spot} from '../../dtos/spot';
import {MapService} from '../../services/map.service';

@Component({
  selector: 'app-view-spots',
  templateUrl: './view-spots.component.html',
  styleUrls: ['./view-spots.component.scss']
})
export class ViewSpotsComponent implements OnInit {
  spots: Spot[];
  constructor(private mapService: MapService) { }

  ngOnInit(): void {
    this.spots = this.mapService.getSpots();
  }
  setSpots(spots: Spot[]) {
    this.spots = spots;
  }

}
