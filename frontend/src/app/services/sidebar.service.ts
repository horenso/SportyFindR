import {Injectable} from '@angular/core';
import {MLocation} from '../util/m-location';
import {MLocSpot} from '../util/m-loc-spot';
import { toUpper } from 'lodash';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {

  public markerLocation: MLocation = null;
  public spot: MLocSpot = null;

  constructor() {
  }
}
