import {Component, OnInit, ViewChild} from '@angular/core';
import {User} from "../../dtos/user";
import {UserService} from "../../services/user.service";
import {Role} from "../../dtos/role";
import {RoleService} from "../../services/role.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {NotificationService} from "../../services/notification.service";

@Component({
  selector: 'app-user-manager',
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.scss']
})
export class UserManagerComponent implements OnInit {

  users: User[];
  user: User;

  roles: Role[];

  userForm: FormGroup;

  userTableColumns: string[] = ['ID', 'Name', 'Email', 'Enabled', "Edit", "Delete"];

  @ViewChild('userTable') userTable;

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

  private getAllRoles() {
    this.roleService.getAllRoles().subscribe(
      (roles: Role[]) => {
        this.roles = roles;
        const editIndex: number = this.userTableColumns.indexOf("Edit");
        for (let i = 0; i < this.roles.length; i++) {
          this.userTableColumns.splice(editIndex + i, 0, this.roles[i].name);
        }
      },
      error => {
        console.log("Couldn't retrieve roles from backend. ", error);
      }
    )

  }

  createUser(user: User) {
    this.userService.createUser(user).subscribe(
      (user: User) => {
        this.users.push(user);
        this.userTable.renderRows();
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
          this.userTable.renderRows();
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
          this.userTable.renderRows();
        }
      },
      error => {
        console.log("Couldn't update user ", user, ". ", error);
      }
    )
  }

  private initUserForm() {
    this.userForm = this.formBuilder.group({
      userName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(15)]],
      userEmail: [null, [Validators.required, Validators.email]],
      userPassword: [null, [Validators.required, Validators.minLength(7)]],
      userEnabled: [false],
      userRoles: [null]
    })
    this.user = null;
  }

  onConfirm(): void {
    const val = this.userForm.value;

    if (this.user == null) {
      const newUser = new User(null, val.userName, val.userEmail, val.userPassword, val.userEnabled, val.userRoles);
      this.createUser(newUser);
    } else {
      const updateUser = new User(val.id, val.userName, val.userEmail, val.userPassword, val.userEnabled, val.userRoles);
      this.updateUser(updateUser);
    }
  }

  onCancel(): void {
    this.initUserForm();
  }

  constructor(
    private userService: UserService,
    private roleService: RoleService,
    private formBuilder: FormBuilder,
    private notificationService: NotificationService) {

  }

  ngOnInit(): void {
    this.getAllUsers();
    this.getAllRoles();
    this.initUserForm();
  }

  hasRole(u: User, r: Role): boolean {
    for (let role of u.roles) {
      if (role.id == r.id) {
        return true;
      }
    }
    return false;
  }

  editUser(u: User) {
    this.user = u;
    this.userForm.patchValue({
      userName: this.user.name,
      userEmail: this.user.email,
      userEnabled: this.user.enabled,
      userRoles: this.user.roles
    })
  }
}
