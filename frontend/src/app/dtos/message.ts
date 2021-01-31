import {ReactionType} from './reaction';

export class Message {
  constructor(
    public id: number,
    public content: string,
    public publishedAt: Date,
    public owner: {id: number, name: string, email: string},
    public spotId: number,
    public ownerReaction: ReactionType,
    public ownerReactionId: number,
    public upVotes?: number,
    public downVotes?: number,
    public expirationDate?: Date) {
  }
}
