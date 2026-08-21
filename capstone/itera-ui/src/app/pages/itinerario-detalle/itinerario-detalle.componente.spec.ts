import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItinerarioDetalleComponente } from './itinerario-detalle.componente';

describe('ItinerarioDetalleComponente', () => {
  let component: ItinerarioDetalleComponente;
  let fixture: ComponentFixture<ItinerarioDetalleComponente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItinerarioDetalleComponente],
    }).compileComponents();

    fixture = TestBed.createComponent(ItinerarioDetalleComponente);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
