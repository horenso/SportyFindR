import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CreateNewSpotComponent} from './create-new-spot.component';

describe('CreateNewSpotComponent', () => {
  let component: CreateNewSpotComponent;
  let fixture: ComponentFixture<CreateNewSpotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateNewSpotComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateNewSpotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
