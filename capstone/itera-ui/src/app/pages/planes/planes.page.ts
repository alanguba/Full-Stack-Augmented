import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatListModule } from '@angular/material/list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { BuscaLugaresInputComponent } from '../../components/busca-lugares-input/busca-lugares-input.component';
import { BuscarPaisInputComponent } from '../../components/buscar-pais-input/buscar-pais-input.component';
import { ListaLugaresComponent } from '../../components/lista-lugares/lista-lugares.component';
import { DatosViajeComponent } from '../../components/datos-viaje/datos-viaje.component';
import { MapaComponent } from '../../components/mapa/mapa.component';
import { PlaceItem } from '../../models/interfaces/PlaceItem.interface';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  from,
  map,
  mapTo,
  merge,
  Observable,
  of,
  Subject,
  switchMap,
  tap,
} from 'rxjs';
import { GooglePlacesService, PlaceSuggestionVM } from '../../services/google-places.service';
import { TripDestination } from '../../models/interfaces/TripDestination.interface';
import { TripPlanStore } from '../../services/trip-plan.service';
import { TripPlanInput } from '../../models/interfaces/TripPlanInput.interface';

@Component({
  selector: 'app-planes.page',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule,
    MatListModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonToggleModule,
    FormsModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    BuscaLugaresInputComponent,
    BuscarPaisInputComponent,
    ListaLugaresComponent,
    DatosViajeComponent,
    MapaComponent,
  ],
  templateUrl: './planes.page.html',
  styleUrl: './planes.page.css',
})
export class PlanesPage implements OnInit {
  // 1) Destino principal
  destination: TripDestination | null = null;

  // 2) Ciudad
  cityCtrl = new FormControl<string | PlaceSuggestionVM>('', { nonNullable: true });

  citySuggestions$: Observable<PlaceSuggestionVM[]> = of([]);

  loadingCity = false;
  errorCity: string | null = null;

  displayCity = (value: string | PlaceSuggestionVM | null) =>
    typeof value === 'string' ? value : (value?.text ?? '');

  // 3) Lugares a visitar (restringidos a la ciudad)
  placeCtrl = new FormControl<string>('', { nonNullable: true });

  placeSuggestions$: Observable<PlaceSuggestionVM[]> = of([]);

  loadingSuggestions = false;
  placesError: string | null = null;

  displaySuggestion = (s: PlaceSuggestionVM) => s?.text ?? '';

  // Selección (lo que el usuario va agregando)
  selected: PlaceItem[] = [];

  private resetPlaceSuggestions$ = new Subject<void>();

  // Datos del viaje
  daysControl = new FormControl<number>(3, { nonNullable: true });
  paceControl = new FormControl<'Relajado' | 'Balanceado' | 'Intenso'>('Balanceado', {
    nonNullable: true,
  });

  constructor(
    private places: GooglePlacesService,
    private tripPlanStore: TripPlanStore,
    private router: Router,
  ) {}

  ngOnInit(): void {
    // crea token de sesión al entrar (opcional)
    this.places.newSessionToken().catch(() => {});

    // City suggestions
    this.cityCtrl.valueChanges.subscribe((value) => {
      if (this.destination && typeof value === 'string') {
        // el usuario ya está editando, invalidamos la selección previa
        this.destination = null;
        this.clearAll();
      }
    });

    this.citySuggestions$ = this.cityCtrl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      map((v) => (typeof v === 'string' ? v.trim() : (v?.text ?? '').trim())),
      filter((text) => text.length >= 2),
      switchMap((text) =>
        from(
          this.places.fetchSuggestions(text, {
            includedPrimaryTypes: ['(cities)'],
            language: 'es-419',
          }),
        ).pipe(catchError(() => of([]))),
      ),
    );

    // Place suggestions
    this.placeSuggestions$ = merge(
      this.resetPlaceSuggestions$.pipe(mapTo([])),
      this.placeCtrl.valueChanges.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => {
          this.placesError = null;
          this.loadingSuggestions = true;
        }),
        filter((value) => (value ?? '').trim().length >= 2),
        switchMap((value) =>
          from(
            this.places.fetchSuggestions(value.trim(), {
              includedPrimaryTypes: [],
              locationRestriction: this.destination?.bounds,
              language: 'es-419',
            }),
          ).pipe(
            catchError((err) => {
              this.placesError =
                'No se pudieron cargar sugerencias. Revisa tu API key / billing / APIs habilitadas.';
              return of([]);
            }),
          ),
        ),
        tap(() => (this.loadingSuggestions = false)),
      ),
    );

    //Deshabilitar búsqueda de lugares hasta que se seleccione una ciudad
    this.placeCtrl.disable({ emitEvent: true });
  }

  isSelected(place: PlaceItem): boolean {
    return this.selected.some((p) => p.id === place.id);
  }

  addPlace(place: PlaceItem): void {
    if (this.isSelected(place)) return;
    this.selected = [...this.selected, place];
  }

  clearAll(): void {
    this.selected = [];
  }

  onPlaceSelected(place: PlaceItem): void {
    this.addPlace(place);
  }

  onDestinationChange(destination: TripDestination | null): void {
    if (destination === null) {
      this.placeCtrl.disable({ emitEvent: true });
      this.clearAll();
    } else {
      this.placeCtrl.enable({ emitEvent: false });
    }
    this.destination = destination;
    this.resetPlaceSuggestions$.next();
  }

  goToItinerary(): void {
    if (!this.destination || this.selected.length === 0) return;

    const plan: TripPlanInput = {
      destination: this.destination, // tu objeto destino actual
      days: this.daysControl.value,
      pace: this.paceControl.value,
      places: this.selected,
    };

    this.tripPlanStore.setPlan(plan);
    this.router.navigateByUrl('/home/itinerario');
  }
}
