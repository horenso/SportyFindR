import {Injectable} from '@angular/core';
import {MLocation} from '../util/m-location';
import {MLocSpot} from '../util/m-loc-spot';
import {Subject} from 'rxjs';

export interface VisibilityFocusChange {
  isVisible: boolean;
  locationInFocus?: MLocation;
}

export enum SidebarState { Open, Opening, Closed, Closing }

@Injectable({
  providedIn: 'root'
})
export class SidebarService {

  public sidebarState: SidebarState = null;

  private changeVisibilityAndFocusSubject = new Subject<VisibilityFocusChange>();

  /**
   * The visibility of the sidebar and/or the MLocation in focus changed,
   * this needs to happen simultaneously because the map changes in size with a delay
   */
  public changeVisibilityAndFocusObservable = this.changeVisibilityAndFocusSubject.asObservable();

  public markerLocation: MLocation = null;
  public spot: MLocSpot = null;

  constructor() {
  }

  public changeVisibilityAndFocus(change: VisibilityFocusChange): void {
    this.changeVisibilityAndFocusSubject.next(change);
  }
}
