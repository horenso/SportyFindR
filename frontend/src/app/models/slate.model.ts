export interface Slate<T> {
  content: T[];
  size: number;
  numberOfElements: number;
  number: number;
  last: boolean;
  first: boolean;
  empty: boolean;
}
