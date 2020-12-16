import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'messageDate'
})
export class MessageDatePipe implements PipeTransform {

  static timeFormat = 'HH:mm';
  static dateFormat = 'm/d/y, HH:mm';

  transform(value: string): string {
    var now = Date.now();
    var then = new Date(value);
    
    var datePipe = new DatePipe('en-US');
    var differenceDateDays = then.getDay() - new Date(now).getDay();

    if (differenceDateDays === 0) {
      return 'today, ' + datePipe.transform(value, MessageDatePipe.timeFormat);
    } else if (differenceDateDays === 1) {
      return 'yesterday, ' + datePipe.transform(value, MessageDatePipe.timeFormat);
    } else {
      return datePipe.transform(value, MessageDatePipe.dateFormat);
    }
  }
}
