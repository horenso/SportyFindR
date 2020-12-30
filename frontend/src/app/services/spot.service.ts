import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable, Subject} from 'rxjs';
import {Spot} from '../dtos/spot';
import {MLocSpot} from '../util/m-loc-spot';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SpotService {

  private spotBaseUri: string = this.globals.backendUri + '/spots';
  private eventSource: EventSource = null;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  private static translateToMLocSpot(spot: Spot): MLocSpot {
    console.log(spot);
    console.log(spot instanceof Spot);
    return new MLocSpot(spot);
  }

  /**
   * Persists spot to the backend
   * @param mLocSpot to persist
   */
  createSpot(mLocSpot: MLocSpot): Observable<MLocSpot> {
    console.log('Create spot with name ' + mLocSpot.name);
    return this.httpClient.post<Spot>(this.spotBaseUri, mLocSpot.toSpot()).pipe(
      map(
        (spot: Spot) => SpotService.translateToMLocSpot(spot)
      )
    );
  }

  deleteById(id: number): Observable<{}> {
    console.log('Delete spot with id ' + id);
    return this.httpClient.delete<Spot>(this.spotBaseUri + '/' + id);
  }

  updateSpot(mLocSpot: MLocSpot): Observable<MLocSpot> {
    console.log('Update spot with name ' + mLocSpot.name);
    return this.httpClient.put<Spot>(this.spotBaseUri, mLocSpot.toSpot()).pipe(
      map(
        (spot: Spot) => SpotService.translateToMLocSpot(spot)
      )
    );
  }

  getSpotsByLocation(locationId: number): Observable<MLocSpot[]> {
    return this.httpClient.get<Spot[]>(this.spotBaseUri + '?location=' + locationId).pipe(
      map(
        (spots: Spot[]) => this.translateToMLocSpots(spots)
      )
    );
  }

  getSpotById(spotId: number): Observable<MLocSpot> {
    return this.httpClient.get<Spot>(this.spotBaseUri + '/' + spotId).pipe(
      map(
        (spot: Spot) => SpotService.translateToMLocSpot(spot)
      )
    );
  }

  observeEvents(spotId: number): Subject<any> {
    console.log('New SSE connection with spot ' + spotId);
    this.closeConnection();
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

  private translateToMLocSpots(spots: Spot[]): MLocSpot[] {
    const mLocSpots: MLocSpot[] = [];
    spots.forEach(
      (spot: Spot) => {
        mLocSpots.push(SpotService.translateToMLocSpot(spot));
      }
    );
    return mLocSpots;
  }
}
