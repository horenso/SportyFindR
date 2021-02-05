import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {FilterLocation} from '../dtos/filter-location';
import {FilterMessage} from '../dtos/filter-message';

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  public currentFilterMessage: FilterMessage = {categoryId: null, hashtag: null, user: null, time: null, page: 0, size: 10};
  public currentFilterLocation: FilterLocation = {radiusEnabled: false, radiusBuffered: false};

  private updateMessageFilterSubject = new BehaviorSubject<FilterMessage>(this.currentFilterMessage);
  public updateMessageFilterObservable = this.updateMessageFilterSubject.asObservable();

  private filterLocationSubject = new BehaviorSubject<FilterLocation>(this.currentFilterLocation);
  public filterLocationObservable = this.filterLocationSubject.asObservable();

  constructor() {
  }

  public updateFilterLocation(filterLocation: FilterLocation): void {
    this.currentFilterLocation = filterLocation;
    this.filterLocationSubject.next(filterLocation);
  }

  public updateMessageFilter(filterMessage: FilterMessage): void {
    this.currentFilterMessage = filterMessage;
    this.updateMessageFilterSubject.next(filterMessage);
  }
}
