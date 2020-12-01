import {Location} from './location';

export class SpotModel {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public location: Location
    ) {
  }
}
