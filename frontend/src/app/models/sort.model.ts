export interface Sort {
  sortby: string;
  direction: SortDirection;
}

export enum SortDirection {
  DESC = 'DESC',
  ASC = 'ASC',
}
