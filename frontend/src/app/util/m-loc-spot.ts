import {Category} from '../dtos/category';
import {MLocation} from './m-location';
import {Spot} from '../dtos/spot';
import {User} from '../dtos/user';

export class MLocSpot {
  public id: number;
  public name: string;
  public description: string;
  public category: Category;
  public markerLocation: MLocation;
  public owner: {id: number, name: string, email: string};

  constructor(id: number, name: string, description: string, category: Category, markerLocation: MLocation, user: User)
  constructor(spot: Spot)
  constructor(spotOrId?: Spot | number, name?: string, description?: string, category?: Category, markerLocation?: MLocation, owner?: User) {
    // This is not very elegant but atm we only have two cases so let's use that to our advantage
    // Problem is, that type checking only works for primitive types but not for self defined classes
    // Also instanceof does only work if the constructor is used
    // we would have to go for a more complicated type check solution like here:
    // https://medium.com/ovrsea/checking-the-type-of-an-object-in-typescript-the-type-guards-24d98d9119b0
    if (typeof spotOrId === 'number' || typeof spotOrId === 'undefined' || spotOrId === null) {
      if (typeof spotOrId === 'number') {
        this.id = spotOrId;
      } else {
        this.id = null;
      }
      if (typeof name === 'string') {
        this.name = name;
        this.description = description;
        this.category = category;
        this.markerLocation = markerLocation;
        this.owner = owner;
      } else {
        this.name = null;
        this.description = null;
        this.category = null;
        this.markerLocation = null;
        this.owner = null;
      }
    } else {
      this.id = spotOrId.id;
      this.name = spotOrId.name;
      this.description = spotOrId.description;
      this.category = spotOrId.category;
      this.markerLocation = new MLocation(spotOrId.location);
      this.owner = spotOrId.owner;
    }
  }

  public toSpot(): Spot {
    return new Spot(this.id, this.name, this.description, this.category, this.markerLocation.toLocation(), this.owner);
  }
}
