import {Marker} from 'leaflet';
import {Location} from '../dtos/location';

export class MarkerLocation extends Marker {
  constructor(location: Location) {
    super([location.latitude, location.longitude]);
    this.id = location.id;
  }

  public id: number;

  changeToLocation() {
    return new Location(this.id, this.getLatLng().lat, this.getLatLng().lng);
  }
}
