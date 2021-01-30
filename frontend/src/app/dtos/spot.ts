import {Category} from './category';
import {Location} from './location';
import {User} from './user';

export class Spot {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public category: Category,
    public location: Location,
    public owner: {id: number, name: string, email: string}) {
  }
}
