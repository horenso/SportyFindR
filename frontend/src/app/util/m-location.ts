import {Icon, icon, Marker} from 'leaflet';
import {Location} from '../dtos/location';

export enum IconType {
  Default,
  New,
  Edit
}

export class MLocation extends Marker {

  static iconDefault: Icon = icon({
    iconRetinaUrl: 'assets/markers/marker-default-2x.png',
    iconUrl: 'assets/markers/marker-default.png',
    shadowUrl: 'assets/markers/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    tooltipAnchor: [16, -28],
    shadowSize: [41, 41]
  });

  static iconNew: Icon = icon({
    iconRetinaUrl: 'assets/markers/marker-new-2x.png',
    iconUrl: 'assets/markers/marker-new.png',
    shadowUrl: 'assets/markers/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    tooltipAnchor: [16, -28],
    shadowSize: [41, 41]
  });

  static iconEdit: Icon = icon({
    iconRetinaUrl: 'assets/markers/marker-edit-2x.png',
    iconUrl: 'assets/markers/marker-edit.png',
    shadowUrl: 'assets/markers/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    tooltipAnchor: [16, -28],
    shadowSize: [41, 41]
  });

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
    this.setIcon(MLocation.iconDefault);
  }

  public changeIcon(type: IconType): void {
    console.log('in MLoc: changed icon to: ' + type.toString());
    
    switch(type) {
      case IconType.Default: this.setIcon(MLocation.iconDefault); break;
      case IconType.New: this.setIcon(MLocation.iconNew); break;
      case IconType.Edit: this.setIcon(MLocation.iconEdit); break;
    }
  }

  toLocation(): Location {
    return new Location(this.id, this.getLatLng().lat, this.getLatLng().lng);
  }
}
