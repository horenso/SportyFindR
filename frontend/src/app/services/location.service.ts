import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Location} from '../dtos/location';
import {MLocation} from '../util/m-location';
import {FilterLocation} from '../dtos/filter-location';

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
   * @param filterLocation containing the search parameters
   */
  filterLocation(filterLocation: FilterLocation): Observable<MLocation[]> {
    let params = new HttpParams();

    if (filterLocation.categoryId != null) {
      params = params.set('categoryId', filterLocation.categoryId.toString());
    }
    if (filterLocation.coordinates != null) {
      params = params.set('latitude', filterLocation.coordinates.lat.toString());
      params = params.set('longitude', filterLocation.coordinates.lng.toString());
    }
    if (filterLocation.radius != null) {
      params = params.set('radius', filterLocation.radius.toString());
    }
    console.log('Loading locations with params: ' + params.toString());
    return this.httpClient.get<Location[]>(`${this.locationBaseUri}`, {params: params}).pipe(
      map(
        value => this.translateToMarkerLocations(value)
      )
    );
  }
}
