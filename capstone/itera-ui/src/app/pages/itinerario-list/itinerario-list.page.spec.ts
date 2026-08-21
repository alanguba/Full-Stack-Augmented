import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItinerarioListPage } from './itinerario-list.page';

describe('ItinerarioListPage', () => {
  let component: ItinerarioListPage;
  let fixture: ComponentFixture<ItinerarioListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItinerarioListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ItinerarioListPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
