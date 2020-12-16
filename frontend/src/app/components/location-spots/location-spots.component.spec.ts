import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationSpotsComponent } from './location-spots.component';

describe('LocationSpotsComponent', () => {
  let component: LocationSpotsComponent;
  let fixture: ComponentFixture<LocationSpotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LocationSpotsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationSpotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
