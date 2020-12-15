export class Reaction {
  constructor(
    public id: number,
    public publishedAt: Date,
    public type: string,
    public messageId: number
  ) {}
}
