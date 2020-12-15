import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Message} from '../dtos/message';
import {Observable, Subject} from 'rxjs';
import {SpotModel} from '../dtos/spot';

@Injectable({
  providedIn: 'root'
})
export class SpotService {

  private spotBaseUri: string = this.globals.backendUri + '/spots';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Persists spot to the backend
   * @param spot to persist
   */
  createSpot(spot: SpotModel): Observable<SpotModel> {
    console.log('Create spot with name ' + spot.name);
    return this.httpClient.post<SpotModel>(this.spotBaseUri, spot);
  }

  deleteSpot(id: number): Observable<any> {
    console.log('Delete spot with id ' + id);
    return this.httpClient.delete(this.spotBaseUri + '/' + id);
  }

  updateSpot(spot: SpotModel): Observable<SpotModel> {
    console.log('Update spot with name ' + spot.name);
    return this.httpClient.put<SpotModel>(this.spotBaseUri, spot);
  }

  observeEvents(spotId: number): Subject<any> {
    console.log('New SSE connection with spot ' + spotId);
    let eventSource = new EventSource(this.globals.backendUri + '/spots/subscribe?spotId=' + spotId);
    let subscription = new Subject();
    eventSource.addEventListener("message", event => subscription.next(event));
    eventSource.addEventListener("reaction", event => subscription.next(event));
    return subscription;
  }
}
