export class Message {
  constructor(
    public id: number,
    public content: string,
    public publishedAt: Date,
    public spotId: number
    ) {}
}
