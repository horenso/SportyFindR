import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable, of} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {Location} from '../dtos/location';
import {MLocation} from '../util/m-location';
import {Message} from '../dtos/message';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private locationBaseUri: string = `${this.globals.backendUri}/locations`;

  constructor(
    private httpClient: HttpClient,
    private globals: Globals) {
  }

  private static translateToMarkerLocation(location: Location): MLocation {
    return new MLocation(location);
  }

  /**
   * Get one Location by id
   * @param locationId of the location
   * @returns MLocation entity
   */
  public getById(locationId: number): Observable<MLocation> {
    return this.httpClient.get<Location>(`${this.locationBaseUri}/${locationId}`).pipe(
      map(
        (location: Location) => LocationService.translateToMarkerLocation(location)
      )
    );
  }

  /**
   * Loads all locations
   * @returns list of MarkerLocations
   */
  getAll(): Observable<MLocation[]> {
    return this.requestAllLocations().pipe(
      map(
        value => this.translateToMarkerLocations(value)
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

  /**
   * Searches locations from the backend according to search parameters
   * @param str containing the search parameters
   */
  filterLocation(str: string): Observable<Location[]> {
    console.log('Search for locations with parameters: ' + str);
    return this.httpClient.get<Location[]>('http://localhost:8080' + str)
      .pipe(
        tap(_ => console.log(`locations: ` + _.length)),
        catchError(this.handleError<Location[]>('No locations found that fit the parameters.', []))
      );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      console.log(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }
}
