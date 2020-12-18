import {EventEmitter, Injectable} from '@angular/core';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {LayerGroup, Map, Marker} from 'leaflet';
import {LocationService} from './location.service';
import {Location} from '../dtos/location';
import {MarkerLocation} from '../util/marker-location';
import {SpotService} from './spot.service';
import {Spot} from '../dtos/spot';
import {SidebarActionService} from './sidebar-action.service';

@Injectable({
  providedIn: 'root'
})
export class MapService {
}
