import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Location} from '../dtos/location';
import {Observable} from 'rxjs';
import {Reaction} from '../dtos/reaction';
import {Message} from '../dtos/message';

@Injectable({
  providedIn: 'root'
})
export class ReactionService {

  private reactionBaseUri: string = this.globals.backendUri + '/reactions';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createReaction(reaction: Reaction): Observable<Reaction> {
    console.log('Create reaction for message ' + reaction.messageId);
    return this.httpClient.post<Reaction>(this.reactionBaseUri, reaction);
  }

  getReactionsByMessage(messageId: number): Observable<Reaction[]> {
    return this.httpClient.get<Reaction[]>(this.reactionBaseUri + '?message=' + messageId);
  }
}
