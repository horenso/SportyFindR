import {Location} from './location';
import {Category} from "./category";

export class Spot {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public category: Category,
    public location: Location
    ) {
  }
}
