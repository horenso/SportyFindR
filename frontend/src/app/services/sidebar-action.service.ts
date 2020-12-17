import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

export enum SidebarActionType {
  NoAction = 'noAction',
  CreateLocSpot = 'createLocSpot',
  CreateSpot = 'createSpot',
  Success = 'Success',
  Cancelled = 'Cancelled',
  Failed = 'Failed',
  ShowSpotsLoc = 'showSpotsLoc'
}

@Injectable({
  providedIn: 'root'
})
export class SidebarActionService {

  private action = new BehaviorSubject<SidebarActionType>(SidebarActionType.NoAction);
  public action$ = this.action.asObservable();

  constructor() {
  }

  public setActionCreateLocSpot() {
    this.action.next(SidebarActionType.CreateLocSpot);
  }
  public setActionCreateSpot() {
    this.action.next(SidebarActionType.CreateSpot);
  }

  public setNoAction() {
    this.action.next(SidebarActionType.NoAction);
  }

  public setActionSuccess() {
    this.action.next(SidebarActionType.Success);
  }

  public setActionCancelled() {
    this.action.next(SidebarActionType.Cancelled);
  }

  setActionFailed() {
    this.action.next(SidebarActionType.Failed);
  }

  public setActionShowSpotsLoc() {
    this.action.next(SidebarActionType.ShowSpotsLoc);
  }
}
