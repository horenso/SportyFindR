import {Injectable, NgZone} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {SidebarService} from './sidebar.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  /**
   * Some common error messages:
   */
  static spotIdNotInt = 'Spot ID must be a positive integer!';
  static locIdNotInt = 'Location ID must be a positive integer!';
  static errorLoadingSpot = 'Could not load Spot!';
  static errorLoadingLoc = 'Could bot load Location!';
  static errorSavingSpot = 'Something went wrong saving the Spot!';

  constructor(
    private toastr: ToastrService,
    private router: Router,
    private sidebarService: SidebarService,
    private ngZone: NgZone) {
  }

  success(message: string) {
    console.log('Success: ' + message);
    this.toastr.success(message);
  }

  warning(message: string) {
    this.toastr.warning(message);
  }

  error(message: string) {
    this.toastr.error(message);
  }

  info(message: string) {
    this.toastr.info(message);
  }

  navigateHomeAndShowError(message: string) {
    this.error(message);
    this.ngZone.run(() => this.router.navigate(['']));
    setTimeout(() => this.sidebarService.changeVisibilityAndFocus({isVisible: false}));
  }
}
