import {Location} from './location';

export class Spot {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public location: Location
    ) {
  }
}
