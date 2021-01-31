import { LatLng } from "leaflet";

export interface FilterLocation {
  coordinates?: LatLng;
  radiusEnabled: boolean;
  radius?: number;
  categoryId?: number;
}