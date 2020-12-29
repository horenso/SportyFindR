import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable, Subject} from 'rxjs';
import {Spot} from '../dtos/spot';

@Injectable({
  providedIn: 'root'
})
export class SpotService {

  private spotBaseUri: string = this.globals.backendUri + '/spots';
  private eventSource: EventSource = null;

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

  getSpotsByLocation(locationId: number): Observable<Spot[]> {
    return this.httpClient.get<Spot[]>(this.spotBaseUri + '?location=' + locationId);
  }

  getSpotById(spotId: number): Observable<Spot> {
    return this.httpClient.get<Spot>(this.spotBaseUri + '/' + spotId);
  }

  observeEvents(spotId: number): Subject<any> {
    console.log('New SSE connection with spot ' + spotId);

    this.eventSource = new EventSource(this.globals.backendUri + '/spots/subscribe?spotId=' + spotId);
    const subscription = new Subject();
    this.eventSource.addEventListener('message/new', event => subscription.next(event));
    this.eventSource.addEventListener('message/delete', event => subscription.next(event));
    this.eventSource.addEventListener('message/updateReaction', event => subscription.next(event));
    return subscription;
  }

  closeConnection(): void {
    if (this.eventSource != null) {
      this.eventSource.close();
    }
    this.eventSource = null;
  }
}
