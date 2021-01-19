import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {User} from "../dtos/user";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUri: string = this.globals.backendUri + '/users';

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
    console.log("Create user", JSON.stringify(user));
    return this.httpClient.post<User>(this.userBaseUri, user);
  }

  public updateUser(user: User): Observable<User> {
    return this.httpClient.put<User>(this.userBaseUri, user);
  }

  public deleteUserById(id: number): Observable<{}> {
    return this.httpClient.delete<User>(this.userBaseUri + '/' + id);
  }

  getUserByEmail(email: string) {
    return this.httpClient.get<User>(this.userBaseUri + '/byEmail' + email);
  }

  constructor(private httpClient: HttpClient, private globals: Globals) { }
}
