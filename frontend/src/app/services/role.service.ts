import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Role} from '../dtos/role';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  private roleBaseUri: string = this.globals.backendUri + '/roles';

  public getAllRoles(): Observable<Role[]> {
    return this.httpClient.get<Role[]>(this.roleBaseUri);
  }

  public createRole(role: Role): Observable<Role> {
    return this.httpClient.post<Role>(this.roleBaseUri, role);
  }

  public deleteRoleById(id: number): Observable<{}> {
    return this.httpClient.delete(this.roleBaseUri + '/' + id);
  }

  constructor(private httpClient: HttpClient, private globals: Globals) { }
}
