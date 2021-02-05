import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthService} from './auth.service';
import {NotificationService} from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class RoleAdminGuardGuard implements CanActivate {

  constructor(private authService: AuthService, private notificationService: NotificationService, private router: Router) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.checkUserRoleAdmin();
  }

  checkUserRoleAdmin(): boolean {
    if (this.authService.isLoggedIn()) {
      if (this.authService.isUserAdmin()) {
        return true;
      } else {
        this.router.navigate(['']).then(() => {});
        this.notificationService.error('Access denied!');
        return false;
      }
    } else {
      this.router.navigate(['']).then(() => {});
      this.notificationService.error('Access denied!');
      return false;
    }
  }

}
