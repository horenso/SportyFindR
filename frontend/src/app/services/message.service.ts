import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Message} from '../dtos/message';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private messageBaseUri: string = this.globals.backendUri + '/messages';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all messages from one spot
   * @param spotId spot to get messages from
   * @returns list of messages
   */
  getMessagesBySpot(spotId: number): Observable<Message[]> {
    return this.httpClient.get<Message[]>(this.messageBaseUri + '?spot=' + spotId);
  }

  /**
   * Saves a new message in a spesific spot
   * @param message to be saved
   * @returns message entity
   */
  saveMessage(message: Message): Observable<Message> {
    return this.httpClient.post<Message>(this.messageBaseUri, message);
  }

  getMessageById(id: number): Observable<Message> {
    console.log('Get message with id ' + id);
    return this.httpClient.get<Message>(this.messageBaseUri + '/' + id);
  }
}
