import {Sort} from './sort.model';

export interface Slate<T> {
  content: T[];
  size: number;
  numberOfElements: number;
  number: number;
  sort: Sort;
  last: boolean;
  first: boolean;
  empty: boolean;
}
