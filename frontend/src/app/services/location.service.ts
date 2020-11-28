import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {LocationModel} from '../dtos/location';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private locationBaseUri: string = this.globals.backendUri + '/locations';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Persists location to the backend
   * @param location to persist
   */
  createLocation(location: LocationModel): Observable<LocationModel> {
    console.log('Create location with title ' + location.id);
    return this.httpClient.post<LocationModel>(this.locationBaseUri, location);
  }
}
