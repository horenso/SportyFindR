import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'distance'
})
export class DistancePipe implements PipeTransform {

  transform(value: number): string {
    if (value < 1000) {
      return `${value} m`;
    } else {
      const km: number = value / 1000;
      return `${km.toFixed(1)} km`;
    }
  }

}
