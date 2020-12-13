import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {Map} from 'leaflet';

@Injectable({
  providedIn: 'root'
})
export class MapService {

  private map = new BehaviorSubject<Map>(null); // this value should be set by the Map Component right in time
  public map$ = this.map.asObservable();

  constructor() { }

  public setMap(map: Map) {
    this.map.next(map);
  }
}
