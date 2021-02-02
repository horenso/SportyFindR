import { TestBed } from '@angular/core/testing';

import { RoleAdminGuardGuard } from './role-admin-guard.guard';

describe('RoleAdminGuardGuard', () => {
  let guard: RoleAdminGuardGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(RoleAdminGuardGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
