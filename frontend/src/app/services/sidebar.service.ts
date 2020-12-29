import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {MLocation} from '../util/m-location';
import {MLocSpot} from '../util/m-loc-spot';

export enum SidebarActionType {
  NoAction = 'noAction',
  CreateLocSpot = 'createLocSpot',
  CreateSpot = 'createSpot',
  Success = 'Success',
  Cancelled = 'Cancelled',
  Failed = 'Failed',
  ShowSpotsLoc = 'showSpotsLoc',
  ShowMessages = 'showMessages'
}

@Injectable({
  providedIn: 'root'
})
export class SidebarService {

  public markerLocation: MLocation = null;
  public spot: MLocSpot = null;

  private action = new BehaviorSubject<SidebarActionType>(SidebarActionType.NoAction);
  public action$ = this.action.asObservable();

  constructor() {
  }

  public setAction(actionType: SidebarActionType) {
    this.action.next(actionType);
  }
}
