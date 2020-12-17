import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable, Subject} from 'rxjs';
import {Spot} from '../dtos/spot';
import {Message} from '../dtos/message';

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
  createSpot(spot: Spot): Observable<Spot> {
    console.log('Create spot with name ' + spot.name);
    return this.httpClient.post<Spot>(this.spotBaseUri, spot);
  }

  deleteById(id: number): Observable<{}> {
    console.log('Delete spot with id ' + id);
    return this.httpClient.delete<Spot>(this.spotBaseUri + '/' + id);
  }

  updateSpot(spot: Spot): Observable<Spot> {
    console.log('Update spot with name ' + spot.name);
    return this.httpClient.put<Spot>(this.spotBaseUri, spot);
  }

  observeEvents(spotId: number): Subject<any> {
    console.log('New SSE connection with spot ' + spotId);
    const eventSource = new EventSource(this.globals.backendUri + '/spots/subscribe?spotId=' + spotId);
    const subscription = new Subject();
    eventSource.addEventListener('message/new', event => subscription.next(event));
    eventSource.addEventListener('message/delete', event => subscription.next(event));
    eventSource.addEventListener('message/updateReaction', event => subscription.next(event));
    return subscription;
  }
}
