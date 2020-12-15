export enum ReactionType {
	THUMBS_UP,
  THUMBS_DOWN,
  NEUTRAL // for changes
}

export class Reaction {
	constructor(
		public id: number,
		public messageId: number,
		public reactionType: ReactionType
	) {}
}