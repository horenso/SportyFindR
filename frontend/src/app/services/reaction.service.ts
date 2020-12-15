import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Reaction} from '../dtos/reaction';

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
    console.log('Get reactions for message with id: ' + messageId);
    return this.httpClient.get<Reaction[]>(this.reactionBaseUri + '?message=' + messageId);
  }

  deleteReactionByMessageAndType(messageId: number, type: string): Observable<any> {
    console.log('Delete reactions of type ' + type + ' for message with id: '  + messageId);
    return this.httpClient.delete(this.reactionBaseUri + '/' + type + '?message=' + messageId);
  }
}
