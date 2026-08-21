import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItinerarioPage } from './itinerario.page';

describe('ItinerarioPage', () => {
  let component: ItinerarioPage;
  let fixture: ComponentFixture<ItinerarioPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItinerarioPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ItinerarioPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
