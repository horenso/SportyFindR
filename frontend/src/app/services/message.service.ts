import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Message} from '../dtos/message';
import {Observable, of} from 'rxjs';
import {Globals} from '../global/globals';
import {catchError, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private messageBaseUri: string = `${this.globals.backendUri}/messages`;

  constructor(
    private httpClient: HttpClient,
    private globals: Globals) {
  }

  /**
   * Loads all messages from one spot
   * @param spotId spot to get messages from
   * @returns list of messages
   */
  getBySpotId(spotId: number): Observable<Message[]> {
    console.log('Get all messages from spot: ' + spotId);
    const params = new HttpParams().set('spot', spotId.toString());
    return this.httpClient.get<Message[]>(this.messageBaseUri, {params: params});
  }

  /**
   * Saves a new message in a spesific spot
   * @param message to be saved
   * @returns message entity
   */
  create(message: Message): Observable<Message> {
    return this.httpClient.post<Message>(this.messageBaseUri, message);
  }

  /**
   * Get one message by id
   * @param id of te message
   * @returns message entity
   */
  getById(id: number): Observable<Message> {
    console.log('Get message with id ' + id);
    return this.httpClient.get<Message>(`${this.messageBaseUri}/${id}`);
  }

  /**
   * Delete one message by id
   * @param id of the message, that should be deleted
   * @returns an empty object once it concludes
   */
  deleteById(id: number): Observable<{}> {
    console.log('Delete message with id ' + id);
    return this.httpClient.delete<Message>(`${this.messageBaseUri}/${id}`);
  }

  /**
   * Searches messages from the backend according to search parameters
   * @param str containing the search parameters
   */
  filterMessage(str: string): Observable<Message[]> {
    console.log(`Search URL: http://localhost:8080/api/v1/messages${str}`);
    return this.httpClient.get<Message[]>(`http://localhost:8080/api/v1/messages${str}`)
      .pipe(
        tap(_ => console.log(`messages: ` + _.length)),
        catchError(this.handleError<Message[]>('No messages found that fit the parameters.', []))
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
