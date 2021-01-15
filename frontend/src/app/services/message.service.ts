import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Message} from '../dtos/message';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

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
}
