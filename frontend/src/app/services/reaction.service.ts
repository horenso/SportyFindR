import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Reaction} from '../dtos/reaction';

@Injectable({
  providedIn: 'root'
})
export class ReactionService {

  private reactionBaseUri: string = `${this.globals.backendUri}/reactions`;

  constructor(
    private httpClient: HttpClient,
    private globals: Globals) {
  }

  /**
   * Create reation for the message of reaction.messageId
   * @param reaction that should be created
   * @returns reation entity
   */
  create(reaction: Reaction): Observable<Reaction> {
    console.log('Create reaction for message ' + reaction.messageId);
    return this.httpClient.post<Reaction>(this.reactionBaseUri, reaction);
  }

  /**
   * Change one reaction - either from thumbs up to thumbs down or vice versa
   * @param reaction entity with the correct reationId
   * @returns updated reaction entity
   */
  change(reaction: Reaction): Observable<Reaction> {
    console.log('Change reaction for message with id ' + reaction.messageId + ' to ' + reaction.type.toString());
    return this.httpClient.patch<Reaction>(this.reactionBaseUri, reaction);
  }

  /**
   * Delete one Reaction by id
   * @param id of the reaction, that should be deleted
   * @returns empty object once it has been deleted
   */
  deleteById(id: number): Observable<{}> {
    console.log('Delete reaction with id ' + id);
    return this.httpClient.delete<Reaction>(`${this.reactionBaseUri}/id`);
  }
}
