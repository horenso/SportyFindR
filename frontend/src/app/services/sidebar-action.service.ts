import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';

export enum SidebarActionType {
  NoAction = 'noAction',
  CreateLocSpot = 'createLocSpot',
  Success = 'Success',
  Cancelled = 'Cancelled'
}

@Injectable({
  providedIn: 'root'
})
export class SidebarActionService {

  private action = new BehaviorSubject<SidebarActionType>(SidebarActionType.NoAction);
  public action$ = this.action.asObservable();

  constructor() {
  }

  public setActionCreateLocSpot()  {
    this.action.next(SidebarActionType.CreateLocSpot);
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
}
