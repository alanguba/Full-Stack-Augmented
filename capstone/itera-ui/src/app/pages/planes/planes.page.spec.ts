import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanesPage } from './planes.page';

describe('PlanesPage', () => {
  let component: PlanesPage;
  let fixture: ComponentFixture<PlanesPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanesPage],
    }).compileComponents();

    fixture = TestBed.createComponent(PlanesPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
