import {Injectable} from '@angular/core';
import {MLocation} from '../util/m-location';
import {MLocSpot} from '../util/m-loc-spot';
import {BehaviorSubject} from 'rxjs';

export interface VisibilityFocusChange {
  isVisible: boolean;
  locationInFocus?: MLocation;
}

export enum SidebarState { Open, Opening, Closed, Closing }

@Injectable({
  providedIn: 'root'
})
export class SidebarService {

  constructor() {
  }

  private sidebarState: SidebarState = null;

  private changeVisibilityAndFocusSubject = new BehaviorSubject<VisibilityFocusChange>({isVisible: false});

  /**
   * The visibility of the sidebar and/or the MLocation in focus changed,
   * this needs to happen simultaneously because the map changes in size with a delay
   */
  public changeVisibilityAndFocusObservable = this.changeVisibilityAndFocusSubject.asObservable();

  public markerLocation: MLocation = null;
  public spot: MLocSpot = null;

  public setSidebarStateOpen(): void {
    this.sidebarState = SidebarState.Open;
    this.changeVisibilityAndFocusSubject.next({isVisible: true});
  }

  public setSidebarStateClosed(): void {
    this.sidebarState = SidebarState.Closed;
    this.changeVisibilityAndFocusSubject.next({isVisible: false});
  }

  public isSidebarClosed(): boolean {
    return this.sidebarState === SidebarState.Closed;
  }

  public changeVisibilityAndFocus(change: VisibilityFocusChange): void {
    this.changeVisibilityAndFocusSubject.next(change);
  }
}
