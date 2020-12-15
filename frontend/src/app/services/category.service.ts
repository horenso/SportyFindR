import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Category} from '../dtos/category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private categoryBaseUri: string = this.globals.backendUri + '/categories';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all categories
   * @returns list of categories
   */
  getAllCategories(): Observable<Category[]> {
    return this.httpClient.get<[]>(this.categoryBaseUri);
  }
}
