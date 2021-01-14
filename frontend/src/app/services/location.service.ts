import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable,} from 'rxjs';
import {map} from 'rxjs/operators';
import {Location} from '../dtos/location';
import {MLocation} from '../util/m-location';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private locationBaseUri: string = this.globals.backendUri + '/locations';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  private static translateToMarkerLocation(location: Location): MLocation {
    return new MLocation(location);
  }

  public getLocationById(locationId: number): Observable<MLocation> {
    return this.httpClient.get<Location>(this.locationBaseUri + '/' + locationId).pipe(
      map(
        (location: Location) => LocationService.translateToMarkerLocation(location)
      )
    );
  }

  /**
   * Loads all locations
   * @returns list of MarkerLocations
   */
  getAllMarkerLocations(): Observable<MLocation[]> {
    return this.requestAllLocations().pipe(
      map(
        value => this.translateToMarkerLocations(value)
      )
    );
  }

  /**
   * Persists location to the backend
   * @param mLoc to persist
   * @returns persisted markerLocation
   */
  createLocation(mLoc: MLocation): Observable<MLocation> {
    console.log('Create location with title ' + mLoc.id);
    return this.httpClient.post<Location>(this.locationBaseUri, mLoc.toLocation()).pipe(
      map(
        value => LocationService.translateToMarkerLocation(value)
      )
    );
  }

  private translateToMarkerLocations(locations: Location[]): MLocation[] {
    const markerLocations: MLocation[] = [];
    locations.forEach(
      (location: Location) => {
        markerLocations.push(LocationService.translateToMarkerLocation(location));
      }
    );
    return markerLocations;
  }

  private requestAllLocations(): Observable<Location[]> {
    return this.httpClient.get<Location[]>(this.locationBaseUri);
  }
}
