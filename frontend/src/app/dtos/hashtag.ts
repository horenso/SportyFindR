import {Message} from './message';
import {Spot} from './spot';

export class Hashtag {
  constructor(
    public id: number,
    public name: string,
    public messagesList: [Message],
    public spotsList: [Spot]
  ) {
  }
}
