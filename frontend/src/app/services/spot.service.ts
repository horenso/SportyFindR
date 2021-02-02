import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable, Subject} from 'rxjs';
import {Spot} from '../dtos/spot';
import {MLocSpot} from '../util/m-loc-spot';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SpotService {

  constructor(
    private httpClient: HttpClient,
    private globals: Globals) {
  }

  private spotBaseUri: string = `${this.globals.backendUri}/spots`;

  // Current EventSource of a spot subscribtion, there can at most be one subscribtion
  private eventSource: EventSource = null;

  private static translateToMLocSpot(spot: Spot): MLocSpot {
    return new MLocSpot(spot);
  }

  /**
   * Create one new spot
   * @param mLocSpot to persist
   * @returns MLocSpot entity
   */
  create(mLocSpot: MLocSpot): Observable<MLocSpot> {
    console.log('Create spot with name ' + mLocSpot.name);
    return this.httpClient.post<Spot>(this.spotBaseUri, mLocSpot.toSpot()).pipe(
      map(
        (spot: Spot) => SpotService.translateToMLocSpot(spot)
      )
    );
  }

  /**
   * Delete on spot by Id
   * @param id of the spot that should be deleted
   * @returns true if the host location was deleted too, else false
   */
  deleteById(id: number): Observable<boolean> {
    console.log('Delete spot with id ' + id);
    return this.httpClient.delete<any>(`${this.spotBaseUri}/${id}`).pipe(
      map(respone => respone.deletedLocation)
    );
  }

  /**
   * Update on spot, the spot must already exist in the system
   * @param mLocSpot that should be updated
   * @returns new MLocSpot entity of the updated Spot
   */
  update(mLocSpot: MLocSpot): Observable<MLocSpot> {
    console.log('Update spot with name ' + mLocSpot.name);
    return this.httpClient.put<Spot>(this.spotBaseUri, mLocSpot.toSpot()).pipe(
      map(
        (spot: Spot) => SpotService.translateToMLocSpot(spot)
      )
    );
  }

  /**
   * Get a list of all spots from one location
   * @param locationId of the location
   * @returns list of spots, this can never be empty because every location has at least on spot
   */
  getByLocationId(locationId: number): Observable<MLocSpot[]> {
    const params = new HttpParams().set('location', locationId.toString());
    return this.httpClient.get<Spot[]>(this.spotBaseUri, {params: params}).pipe(
      map(
        (spots: Spot[]) => this.translateToMLocSpots(spots)
      )
    );
  }

  /**
   * Get one spot by id
   * @param spotId of the spot
   * @returns a MLocSpot entity
   */
  getById(spotId: number): Observable<MLocSpot> {
    return this.httpClient.get<Spot>(`${this.spotBaseUri}/${spotId}`).pipe(
      map(
        (spot: Spot) => SpotService.translateToMLocSpot(spot)
      )
    );
  }

  /**
   * Get one spot by id
   * @param spotId of the spot
   * @returns a MLocSpot entity
   */
  getSpotById(spotId: number): Observable<Spot> {
    return this.httpClient.get<Spot>(`${this.spotBaseUri}/${spotId}`).pipe(
    );
  }

  /**
   * Open a new SSE connection to observe message events within a spot
   * Events include:
   * 'message/new' if a new message arrieves
   * 'message/delete' if a message was deleted
   * 'message/updateReaction' if a message changed reactions
   * @param spotId of the spot to observe
   * @returns Subject that emits these events
   */
  openSseConnection(spotId: number): Subject<any> {
    console.log('New SSE connection with spot ' + spotId);
    this.closeConnection();
    const params = new HttpParams().set('spotId', spotId.toString());
    this.eventSource = new EventSource(`${this.spotBaseUri}/subscribe?${params.toString()}`);
    const subscription = new Subject();
    this.eventSource.addEventListener('message/new', event => subscription.next(event));
    this.eventSource.addEventListener('message/delete', event => subscription.next(event));
    this.eventSource.addEventListener('message/updateReaction', event => subscription.next(event));
    return subscription;
  }

  /**
   * Close current SSE connection, if one is open
   * Only one SSE connection can be open
   */
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
