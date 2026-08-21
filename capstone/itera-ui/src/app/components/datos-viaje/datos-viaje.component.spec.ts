import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatosViajeComponent } from './datos-viaje.component';

describe('DatosViajeComponent', () => {
  let component: DatosViajeComponent;
  let fixture: ComponentFixture<DatosViajeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DatosViajeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DatosViajeComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
