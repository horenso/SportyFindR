export enum ReactionType {
	THUMBS_UP,
	THUMBS_DOWN
}

export class Reaction {
	constructor(
		public id: number,
		public messageId: number,
		public reactionType: ReactionType
	) {}
}