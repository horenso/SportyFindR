import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Hashtag} from '../dtos/hashtag';
import {SimpleHashtag} from '../dtos/simpleHashtag';

@Injectable({
  providedIn: 'root'
})
export class HashtagService {

  private hashtagBaseUri: string = this.globals.backendUri + '/hashtags';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  public getHashtagByName(name: string): Observable<Hashtag> {
    console.log('Get hashtag with name ' + name);
    return this.httpClient.get<Hashtag>(this.hashtagBaseUri + '/' + name);
  }

  public search(str: string): Observable<SimpleHashtag[]> {
    console.log('Search for hashtags: ' + str);
    const params = new HttpParams()
      .set('name', str);
    return this.httpClient.get<SimpleHashtag[]>(`${this.hashtagBaseUri}/filter`, {params: params});
  }

}
