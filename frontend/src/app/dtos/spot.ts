import {Category} from './category';
import {MarkerLocation} from "../util/marker-location";

export class Spot {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public category: Category,
    public markerLocation: MarkerLocation
  ) {
  }
}
