import {Role} from "./role";

export class User {
  constructor(
    public id: number,
    public name: string,
    public email: string,
    public password: string,
    public enabled: boolean,
    public roles: Role[]
  ) {
  }
}
