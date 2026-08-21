import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DestinoCardComponent } from './destino-card.component';

describe('DestinoCardComponent', () => {
  let component: DestinoCardComponent;
  let fixture: ComponentFixture<DestinoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DestinoCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DestinoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
