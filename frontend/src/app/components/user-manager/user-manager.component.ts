import { Component, OnInit } from '@angular/core';
import {User} from "../../dtos/user";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-user-manager',
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.scss']
})
export class UserManagerComponent implements OnInit {

  private users: User[];

  getAllUsers(): void {
    this.userService.getAllUsers().subscribe(
      (users: User[]) => {
        this.users = users;
      },
      error => {
        console.log("Couldn't retrieve users from backend. ", error);
      }
    )
  }

  createUser(user: User) {
    this.userService.createUser(user).subscribe(
      (user: User) => {
        this.users.push(user);
      },
      error => {
        console.log("Couldn't save user to the backend. ", error);
      }
    )
  }

  deleteUser(user: User) {
    this.userService.deleteUserById(user.id).subscribe(
      next => {
        const index: number = this.users.indexOf(user);
        if (index !== -1) {
          this.users.splice(index, 1);
        }
      },
      error => {
        console.log("Couldn't remove user from the backend. ", error);
      }
    )
  }

  updateUser(user: User) {
    this.userService.updateUser(user).subscribe(
      (updatedUser: User) => {
        const index: number = this.users.indexOf(user);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
      },
      error => {
        console.log("Couldn't update user ", user, ". ", error);
      }
    )
  }

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getAllUsers();
  }

}
