import {Injectable} from '@angular/core';
import {MLocation} from '../util/m-location';
import {MLocSpot} from '../util/m-loc-spot';
import {Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {

  private visibilityChanged = new Subject<boolean>();
  public visibilityChanged$ = this.visibilityChanged.asObservable();

  public markerLocation: MLocation = null;
  public spot: MLocSpot = null;

  constructor() {
  }

  public changeVisibility(isVisible: boolean): void {
    this.visibilityChanged.next(isVisible);
    console.log('change visibibility to :' + isVisible);
  }
}
