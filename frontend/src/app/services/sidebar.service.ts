import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {Spot} from '../dtos/spot';
import {Message} from '../dtos/message';
import {Location} from '../dtos/location';

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

  public location: Location = null;
  public spot: Spot = null;

  private action = new BehaviorSubject<SidebarActionType>(SidebarActionType.NoAction);
  public action$ = this.action.asObservable();

  constructor() {
  }

  public setAction(actionType: SidebarActionType) {
    this.action.next(actionType);
  }
}
