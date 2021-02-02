import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Category} from '../dtos/category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private categoryBaseUri: string = `${this.globals.backendUri}/categories`;

  constructor(
    private httpClient: HttpClient,
    private globals: Globals) {
  }

  /**
   * Loads all categories
   * @returns list of categories
   */
  getAll(): Observable<Category[]> {
    console.log('Get all categories');
    return this.httpClient.get<[]>(`${this.categoryBaseUri}`);
  }
}
