import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuscaLugaresInputComponent } from './busca-lugares-input.component';

describe('BuscaLugaresInputComponent', () => {
  let component: BuscaLugaresInputComponent;
  let fixture: ComponentFixture<BuscaLugaresInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BuscaLugaresInputComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BuscaLugaresInputComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
