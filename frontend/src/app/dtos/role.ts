import {User} from "./user";


export class Role {
  constructor(
    public id: number,
    public name: string,
    public userIds: number
  ) {
  }
}
