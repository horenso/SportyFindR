import {Marker} from 'leaflet';
import {Location} from '../dtos/location';

export class MLocation extends Marker {
  public id: number;

  constructor(location: Location)

  constructor(id: number, latitude: number, longitude: number)

  constructor(locationOrId?: Location | number, latitude?: number, longitude?: number) {
    // This is not very elegant but atm we only have two cases so let's use that to our advantage
    // Problem is, that type checking only works for primitive types but not for self defined classes
    // Also instanceof does only work if the constructor is used
    // we would have to go for a more complicated type check solution like here:
    // https://medium.com/ovrsea/checking-the-type-of-an-object-in-typescript-the-type-guards-24d98d9119b0
    if (typeof locationOrId === 'number' || typeof locationOrId === 'undefined' || locationOrId === null) {
      super([latitude, longitude]);
      if (typeof locationOrId === 'number') {
        this.id = locationOrId;
      } else {
        this.id = null;
      }
    } else {
      super([locationOrId.latitude, locationOrId.longitude]);
      this.id = locationOrId.id;
    }
    this.addClickAction();
  }

  toLocation() {
    return new Location(this.id, this.getLatLng().lat, this.getLatLng().lng);
  }

  private addClickAction() {
    this.on('click', () => {
      // TODO set routing for clicked MarkerLocation
      console.log('Clicked on MarkerLocation: ', this);
    });
  }
}
