import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Message} from '../dtos/message';
import {Observable, Subject} from 'rxjs';
import {Globals} from '../global/globals';
import {Page} from '../models/page.model';
import {FilterMessage} from '../dtos/filter-message';
import {MessagePage} from '../dtos/message-page';
import {DatePipe} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private messageBaseUri: string = `${this.globals.backendUri}/messages`;

  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
    private datePipe: DatePipe) {
  }

  /**
   * Loads all messages from one spot
   * @param spotId spot to get messages from
   * @returns list of messages
   */
  getBySpotId(spotId: number, page: number, size: number): Observable<MessagePage> {
    console.log(`Get one page of messages from spot ${spotId}, page: ${page}, size: ${size}`);
    const params = new HttpParams()
      .set('spotId', spotId.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.httpClient.get<MessagePage>(this.messageBaseUri, {params: params});
  }

  /**
   * Saves a new message in a specific spot
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
   * @param filterMessage containing the search parameters
   */
  filterMessage(filterMessage: FilterMessage): Observable<Page<Message>> {
    let params = new HttpParams()
      .set('page', filterMessage.page.toString())
      .set('size', filterMessage.size.toString());
    
    if (filterMessage.categoryId != null) {
      params = params.set('categoryId', filterMessage.categoryId.toString());
    }
    if (filterMessage.hashtag != null) {
      params = params.set('hashtag', filterMessage.hashtag.toString());
    }
    if (filterMessage.user != null) {
      params = params.set('user', filterMessage.user.toString());
    }
    if (filterMessage.time != null) {
      params = params.set('time', this.datePipe.transform(filterMessage.time, 'yyyy-MM-dd'));
    }

    console.log(params.toString());
    return this.httpClient.get<Page<Message>>(`${this.messageBaseUri}/filter`, {params: params});
  }
}
