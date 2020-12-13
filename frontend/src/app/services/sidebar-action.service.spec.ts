import { TestBed } from '@angular/core/testing';

import { SidebarActionService } from './sidebar-action.service';

describe('SidebarActionService', () => {
  let service: SidebarActionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SidebarActionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
