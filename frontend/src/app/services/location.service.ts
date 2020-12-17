import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Location} from '../dtos/location';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private locationBaseUri: string = this.globals.backendUri + '/locations';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all locations
   * @returns list of locations
   */
  getAllLocations(): Observable<Location[]> {
    return this.httpClient.get<[]>(this.locationBaseUri);
  }

  /**
   * Persists location to the backend
   * @param location to persist
   * @returns persisted location
   */
  createLocation(location: Location): Observable<Location> {
    console.log('Create location with title ' + location.id);
    return this.httpClient.post<Location>(this.locationBaseUri, location);
  }
}
