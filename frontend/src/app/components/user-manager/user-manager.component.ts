import {Component, OnInit, AfterViewInit, ViewChild} from '@angular/core';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {Role} from '../../dtos/role';
import {RoleService} from '../../services/role.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NotificationService} from '../../services/notification.service';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'app-user-manager',
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.scss']
})

export class UserManagerComponent implements OnInit, AfterViewInit {

  users: User[];
  user: User;

  roles: Role[];

  roleTableColumns: string[] = ['Name', 'Delete'];

  userForm: FormGroup;
  roleForm: FormGroup;

  userTableColumns: string[] = ['Name', 'Email', 'Enabled', 'Edit', 'Delete'];

  dataSource: MatTableDataSource<User>;

  @ViewChild('userTable') userTable;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  @ViewChild('roleTable') roleTable;

  getAllUsers(): void {
    this.userService.getAllUsers().subscribe(
      (users: User[]) => {
        this.users = users;
        this.dataSource.data = users;
      },
      error => {
        console.log(`Couldn\'t retrieve users from backend.
        `, error);
      }
    );
  }

  private getAllRoles() {
    this.roleService.getAllRoles().subscribe(
      (roles: Role[]) => {
        this.roles = roles;
        const editIndex: number = this.userTableColumns.indexOf('Edit');
        for (let i = 0; i < this.roles.length; i++) {
          this.userTableColumns.splice(editIndex + i, 0, this.roles[i].name);
        }
      },
      error => {
        console.log('Couldn\'t retrieve roles from backend. ', error);
        this.notificationService.error(`Couldn\'t retrieve roles from backend.
        ` + error.message);
      }
    );

  }

  createUser(user: User) {
    this.userService.createUser(user).subscribe(
      (user: User) => {
        this.users.push(user);
        this.dataSource.data = this.users;
        this.notificationService.success('Created user (ID: ' + user.id + ', Name: ' + user.name + ')');
      },
      error => {
        console.log('Couldn\'t save user to the backend. ', error);
        this.notificationService.error(`Couldn\'t save user to the backend.
        ` + error.message);
      }
    );
  }

  createRole(role: Role) {
    this.roleService.createRole(role).subscribe(
      (role: Role) => {
        this.roles.push(role);
        this.notificationService.success('Created role (ID: ' + role.id + ', Name: ' + role.name + ')');

        this.roleTable.renderRows();

        const enabledIndex: number = this.userTableColumns.indexOf('Enabled');
        const newRoleIndex: number = enabledIndex + 1 + this.roles.indexOf(role);
        this.userTableColumns.splice(newRoleIndex, 0, role.name);
      },
      error => {
        console.log('Couldn\'t save role to the backend. ', error);
        this.notificationService.error(`Couldn\'t save role to the backend.
        ` + error.message);
      }
    );
  }

  deleteRole(r) {
    this.roleService.deleteRoleById(r.id).subscribe(
      next => {
        const index: number = this.roles.indexOf(r);

        if (index !== -1) {
          const enabledIndex: number = this.userTableColumns.indexOf('Enabled');
          const delRoleIndex: number = enabledIndex + 1 + index;
          this.userTableColumns.splice(delRoleIndex, 1);

          this.roles.splice(index, 1);
          this.notificationService.success('Deleted role (ID: ' + r.id + ', Name: ' + r.name + ')');

          this.roleTable.renderRows();
        }
      },
      error => {
        console.log('Couldn\'t remove role from the backend. ', error);
        this.notificationService.error(`Couldn\'t remove role from the backend.
        ` + error.message);
      }
    );
  }


  deleteUser(user: User) {
    this.userService.deleteUserById(user.id).subscribe(
      next => {
        const index: number = this.users.indexOf(user);
        if (index !== -1) {
          this.users.splice(index, 1);
          this.dataSource.data = this.users;
          this.notificationService.success('Deleted user (ID: ' + user.id + ', Name: ' + user.name + ')');
        }
      },
      error => {
        console.log('Couldn\'t remove user from the backend. ', error);
        this.notificationService.error(`Couldn\'t remove user from the backend.
        ` + error.message);
      }
    );
  }

  updateUser(user: User) {
    this.userService.updateUser(user).subscribe(
      (updatedUser: User) => {
        const index: number = this.users.findIndex(
          (el: User) => el.id === updatedUser.id
        );
        if (index !== -1) {
          this.users[index] = updatedUser;
          this.dataSource.data = this.users;
          this.notificationService.success('Updated user (ID: ' + updatedUser.id + ', Name: ' + updatedUser.name + ')');
        }
      },
      error => {
        console.log('Couldn\'t update user ', user, '. ', error);
        this.notificationService.error(`Couldn\'t update user ` + user.id + `.
        ` + error.message);
      }
    );
  }

  private initUserForm(): void {
    this.userForm = this.formBuilder.group({
      userName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(15)]],
      userEmail: [null, [Validators.required, Validators.email]],
      userPassword: [null, null],
      userEnabled: [false],
      userRoleIds: [null]
    });
    this.user = null;
    this.toggleUserPasswordValidator();
  }

  private initRoleForm(): void {
    this.roleForm = this.formBuilder.group({
      roleName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(15)]],
    });
  }

  onConfirm(): void {
    const val = this.userForm.value;

    if (this.user == null) {
      const newUser = new User(null, val.userName, val.userEmail, val.userPassword, val.userEnabled, val.userRoleIds);
      this.createUser(newUser);
    } else {
      const updateUser = new User(this.user.id, val.userName, val.userEmail, val.userPassword, val.userEnabled, val.userRoleIds);
      this.updateUser(updateUser);
    }
    this.initUserForm();
  }

  onConfirmRole(): void {
    const val = this.roleForm.value;

    const newRole = new Role(null, val.roleName, null);
    this.createRole(newRole);
    this.initRoleForm();
  }

  onCancel(): void {
    this.initUserForm();
  }

  onCancelRole(): void {
    this.initRoleForm();
  }

  constructor(
    private userService: UserService,
    private roleService: RoleService,
    private formBuilder: FormBuilder,
    private notificationService: NotificationService) {

  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource<User>();
    this.getAllUsers();
    this.getAllRoles();
    this.initUserForm();
    this.initRoleForm();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  hasRole(u: User, r: Role): boolean {
    for (const roleId of u.roleIds) {
      if (roleId === r.id) {
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
      userRoleIds: this.user.roleIds
    });
    this.toggleUserPasswordValidator();
  }

  private toggleUserPasswordValidator() {
    if (this.user == null) {
      this.userForm.get('userPassword').setValidators([Validators.minLength(7), Validators.required]);
    } else {
      this.userForm.get('userPassword').setValidators(Validators.minLength(7));
    }
    this.userForm.get('userPassword').updateValueAndValidity();
  }
}
