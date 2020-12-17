import {Marker} from 'leaflet';
import {Location} from '../dtos/location';

export class MarkerLocation extends Marker {
  id: number;
  constructor(location: Location) {
    super([location.latitude, location.longitude]);
    this.setId(location.id);
  }
  setId(id: number) {
    this.id = id;
  }
  getId() {
    return this.id;
  }
  changeToLocation() {
    return new Location(this.id, this.getLatLng().lat, this.getLatLng().lng);
  }
}
