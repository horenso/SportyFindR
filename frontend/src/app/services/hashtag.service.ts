import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Hashtag} from '../dtos/hashtag';

@Injectable({
  providedIn: 'root'
})
export class HashtagService {

  private hashtagBaseUri: string = this.globals.backendUri + '/hashtags';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getHashtagByName(name: string): Observable<Hashtag> {
    console.log('Get hashtag with name ' + name);
    return this.httpClient.get<Hashtag>(this.hashtagBaseUri + '/' + name);
  }

  /**
   * Loads all hashtags
   * @returns list of hashtags
   */
  getAll(): Observable<Hashtag[]> {
    console.log('Get all hashtags');
    return this.httpClient.get<[]>(this.hashtagBaseUri);
  }
}
