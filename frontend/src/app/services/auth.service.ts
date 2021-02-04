import {Injectable, OnDestroy, OnInit} from '@angular/core';
import {AuthRequest} from '../dtos/auth-request';
import {BehaviorSubject, Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';
import {LocalStorageService} from 'ngx-webstorage';
import {User} from '../dtos/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authBaseUri: string = this.globals.backendUri + '/authentication';
  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;

  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
    private localStorage: LocalStorageService) {
    this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.retrieve('currentUser')));
    this.setUser();
    this.currentUser = this.currentUserSubject.asObservable();
  }

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   * @param authRequest User data
   */
  loginUser(authRequest: AuthRequest): Observable<string> {
    return this.httpClient.post(this.authBaseUri, authRequest, {responseType: 'text'})
      .pipe(
        tap((authResponse: string) => {
            AuthService.setToken(authResponse);
            this.currentUserSubject.next(jwt_decode(authResponse));
          }
        )
      );
  }

  private setUser(): void {
    if (this.getToken() != null) {
      this.currentUserSubject.next(jwt_decode(this.getToken()));
    }
  }

  public currentUserEmail(): string {
    if (this.getToken() != null) {
      return this.currentUserSubject.value['sub'];
    } else {
      return null;
    }
  }

  public isUserAdmin(): Boolean {
    return !!this.currentUserSubject.value['rol'].some(x => x === 'ROLE_ADMIN');

  }

  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn() {
    return !!this.getToken() && (AuthService.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
  }

  logoutUser() {
    console.log('Logout');
    this.localStorage.clear('username');
    localStorage.removeItem('authToken');
  }

  getToken() {
    return localStorage.getItem('authToken');
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRoles() {
    if (this.getToken() != null) {
      const decoded: any = jwt_decode(this.getToken());
      const authInfo: string[] = decoded.rol;
      return authInfo;
    }
    return 'UNDEFINED';
  }

  private static setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }

  private static getTokenExpirationDate(token: string): Date {

    const decoded: any = jwt_decode(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }
}
