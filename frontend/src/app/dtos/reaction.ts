export enum ReactionType {
	THUMBS_UP = 'THUMBS_UP',
  THUMBS_DOWN = 'THUMBS_DOWN',
  NEUTRAL = 0
}

export class Reaction {
	constructor(
		public id: number,
		public messageId: number,
		public type: ReactionType,
	) {}
}

export class OldReaction {
	constructor(
		public id: number,
		public publishedAt: Date, 
		public type: string,
		public messageId: number,
	) {}
}
