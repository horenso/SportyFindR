import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable, Subject} from 'rxjs';
import {map} from 'rxjs/operators';
import {Spot} from '../dtos/spot';
import {MLocSpot} from "../util/m-loc-spot";


@Injectable({
  providedIn: 'root'
})
export class SpotService {

  private spotBaseUri: string = this.globals.backendUri + '/spots';

  constructor(private httpClient: HttpClient, private globals: Globals) {
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

  observeEvents(spotId: number): Subject<any> {
    console.log('New SSE connection with spot ' + spotId);
    const eventSource = new EventSource(this.globals.backendUri + '/spots/subscribe?spotId=' + spotId);
    const subscription = new Subject();
    eventSource.addEventListener('message/new', event => subscription.next(event));
    eventSource.addEventListener('message/delete', event => subscription.next(event));
    eventSource.addEventListener('message/updateReaction', event => subscription.next(event));
    return subscription;
  }

  private translateToMLocSpots(spots: Spot[]): MLocSpot[] {
    const mLocSpots: MLocSpot[] = [];
    spots.forEach(
      (spot: Spot) => {
        mLocSpots.push(SpotService.translateToMLocSpot(spot));
      }
    )
    return mLocSpots;
  }

  private static translateToMLocSpot(spot: Spot): MLocSpot {
    console.log(spot);
    console.log(spot instanceof Spot);
    return new MLocSpot(spot);
  }
}
