import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CreateNewLocationAndSpotComponent} from './create-new-location-and-spot.component';

describe('CreateNewLocationAndSpotComponent', () => {
  let component: CreateNewLocationAndSpotComponent;
  let fixture: ComponentFixture<CreateNewLocationAndSpotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateNewLocationAndSpotComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateNewLocationAndSpotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
