import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {User} from "../dtos/user";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from "rxjs";
import {SimpleUser} from '../dtos/simpleUser';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUri: string = this.globals.backendUri + '/users';
  private activeUser: string;

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  public getAllUsers(): Observable<User[]> {
    return this.httpClient.get<User[]>(this.userBaseUri);
  }

  public getUserById(id: number): Observable<User> {
    return this.httpClient.get<User>(this.userBaseUri + '/' + id);
  }

  public getUsersByRoleId(roleId: number): Observable<User[]> {
    return this.httpClient.get<User[]>(this.userBaseUri + '/byRole/' + roleId);
  }

  public createUser(user: User): Observable<User> {
    return this.httpClient.post<User>(this.userBaseUri, user);
  }

  public updateUser(user: User): Observable<User> {
    return this.httpClient.put<User>(this.userBaseUri, user);
  }

  public deleteUserById(id: number): Observable<{}> {
    return this.httpClient.delete<User>(this.userBaseUri + '/' + id);
  }

  getUserByEmail(email: string) {
    return this.httpClient.get<User>(this.userBaseUri + '/byEmail/' + email);
  }

  public search(str: string): Observable<SimpleUser[]> {
    console.log('Search for user: ' + str);
    const params = new HttpParams()
      .set('name', str);
    return this.httpClient.get<SimpleUser[]>(`${this.userBaseUri}/filter`, {params: params});
  }

}
