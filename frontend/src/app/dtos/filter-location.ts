import {LatLng} from 'leaflet';

export interface FilterLocation {
  coordinates?: LatLng;
  radiusEnabled: boolean;
  radius?: number;
  radiusBuffered: boolean;
  categoryId?: number;
  hashtag?: string;
}
