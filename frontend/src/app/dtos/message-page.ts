import {Message} from './message';

export interface MessagePage {
  content: Message[];
  pageable: {
    offset: number;
    pageNumber: number;
    pageSize: number;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  number: number;
  size: number;
  first: boolean;
  numberOfElements: number;
  empty: false;
}
