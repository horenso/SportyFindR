import {Slate} from './slate.model';

export interface Page<T> extends Slate<T> {
  totalElements: number;
}
