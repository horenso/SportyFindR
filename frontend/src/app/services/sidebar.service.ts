import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {Spot} from '../dtos/spot';
import {Message} from '../dtos/message';
import {MLocation} from "../util/m-location";

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

  public markerLocation: MLocation;
  public spot: Spot;
  public messageList: Message[];

  private action = new BehaviorSubject<SidebarActionType>(SidebarActionType.NoAction);
  public action$ = this.action.asObservable();

  constructor() {
  }

  public setAction(actionType: SidebarActionType) {
    this.action.next(actionType);
  }
}
