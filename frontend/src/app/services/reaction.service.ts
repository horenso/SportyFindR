import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {OldReaction, Reaction, ReactionType} from '../dtos/reaction';

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

  changeReaction(reaction: Reaction): Observable<Reaction> {
    console.log('Change reaction for message with id ' + reaction.messageId + ' to ' + reaction.type.toString());
    return this.httpClient.patch<Reaction>(this.reactionBaseUri, reaction);
  }

  deleteById(id: number): Observable<{}> {
    console.log('Delete reaction with id ' + id);
    return this.httpClient.delete<Reaction>(this.reactionBaseUri + '/' + id);
  }
}
