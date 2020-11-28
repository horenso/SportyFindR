import { TestBed } from '@angular/core/testing';

import { SpotService } from './spot.service';

describe('SpotService', () => {
  let service: SpotService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SpotService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
