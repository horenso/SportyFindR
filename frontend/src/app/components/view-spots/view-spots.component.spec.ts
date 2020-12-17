import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewSpotsComponent } from './view-spots.component';

describe('ViewSpotsComponent', () => {
  let component: ViewSpotsComponent;
  let fixture: ComponentFixture<ViewSpotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewSpotsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewSpotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
