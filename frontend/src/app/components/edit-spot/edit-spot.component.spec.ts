import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSpotComponent } from './edit-spot.component';

describe('EditSpotComponent', () => {
  let component: EditSpotComponent;
  let fixture: ComponentFixture<EditSpotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditSpotComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditSpotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
