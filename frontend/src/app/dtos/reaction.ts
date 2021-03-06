export enum ReactionType {
  THUMBS_UP = 'THUMBS_UP',
  THUMBS_DOWN = 'THUMBS_DOWN',
  NEUTRAL = 'NEUTRAL'
}

export class Reaction {
  constructor(
    public id: number,
    public messageId: number,
    public type: ReactionType,
    public owner: {id: number, name: string, email: string}) {
  }
}
