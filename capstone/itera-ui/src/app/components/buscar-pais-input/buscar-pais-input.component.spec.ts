import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuscarPaisInputComponent } from './buscar-pais-input.component';

describe('BuscarPaisInputComponent', () => {
  let component: BuscarPaisInputComponent;
  let fixture: ComponentFixture<BuscarPaisInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BuscarPaisInputComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BuscarPaisInputComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
