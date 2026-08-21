import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Observable } from 'rxjs';
import { GooglePlacesService, PlaceSuggestionVM } from '../../services/google-places.service';
import { TripDestination } from '../../models/interfaces/TripDestination.interface';
import { BoundsLiteral } from '../../models/interfaces/BoundsLiteral.interface';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { CommonModule } from '@angular/common';
import { MatIconButton } from '@angular/material/button';

@Component({
  selector: 'app-buscar-pais-input',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatIconButton,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
  ],
  templateUrl: './buscar-pais-input.component.html',
  styleUrl: './buscar-pais-input.component.css',
})
export class BuscarPaisInputComponent {
  @Input() cityCtrl!: FormControl<string | PlaceSuggestionVM>;
  @Input() citySuggestions$!: Observable<PlaceSuggestionVM[]>;
  @Input() displayCity!: (value: string | PlaceSuggestionVM | null) => string;

  @Output() destinationChange = new EventEmitter<TripDestination | null>();
  destination: TripDestination | null = null;

  constructor(private places: GooglePlacesService) {}

  // Helpers

  private boundsFromCenter(lat: number, lng: number, kmRadius: number): BoundsLiteral {
    // Aproximación simple: 1° lat ≈ 111km
    const deltaLat = kmRadius / 111;
    const deltaLng = kmRadius / (111 * Math.cos((lat * Math.PI) / 180));

    return {
      west: lng - deltaLng,
      east: lng + deltaLng,
      south: lat - deltaLat,
      north: lat + deltaLat,
    };
  }

  private boundsFromViewportOrFallback(
    viewport: any,
    center?: { lat: number; lng: number },
  ): BoundsLiteral | undefined {
    // viewport en el modelo REST existe como propiedad, pero en JS puede variar. [4](https://developers.google.com/maps/documentation/places/web-service/reference/rest/v1/places)
    // Intentamos leerlo de forma segura.
    try {
      if (viewport?.getSouthWest && viewport?.getNorthEast) {
        const sw = viewport.getSouthWest();
        const ne = viewport.getNorthEast();
        return { west: sw.lng(), south: sw.lat(), east: ne.lng(), north: ne.lat() };
      }

      // Algunos runtimes lo exponen como { low: {lat,lng}, high: {lat,lng} }
      if (viewport?.low && viewport?.high) {
        return {
          west: viewport.low.lng,
          south: viewport.low.lat,
          east: viewport.high.lng,
          north: viewport.high.lat,
        };
      }
    } catch {}

    // Fallback: bounding box ~ 25km alrededor del centro
    if (!center) return undefined;
    return this.boundsFromCenter(center.lat, center.lng, 25);
  }

  // Selección de ciudad
  async onCitySelected(s: PlaceSuggestionVM) {
    if (!s?.prediction) return;

    // 1) Obtener detalles del place “ciudad”
    const place = s.prediction.toPlace();

    // Intentamos obtener location y viewport (si está disponible en tu runtime).
    // En el modelo REST de Place existe viewport. [4](https://developers.google.com/maps/documentation/places/web-service/reference/rest/v1/places)
    await place.fetchFields({
      fields: ['displayName', 'formattedAddress', 'location', 'viewport', 'addressComponents'],
    });

    const center = place.location
      ? { lat: place.location.lat(), lng: place.location.lng() }
      : undefined;

    // 2) Construir bounds:
    // - Si viewport existe: úsalo
    // - Si no: calcula un bounding box alrededor del centro (fallback)
    const bounds = this.boundsFromViewportOrFallback(place.viewport, center);

    this.destination = {
      label: place.formattedAddress || place.displayName || s.text,
      placeId: s.placeId,
      center,
      bounds,
    };
    this.destinationChange.emit(this.destination);

    // 3) Limpia el input de lugares y “habilita” la búsqueda
    // this.placeCtrl.setValue('');
  }


  clearDestination() {
    this.resetDestinationState();
    this.cityCtrl.setValue('', { emitEvent: true }); // deja que vuelva a sugerir
    //deshabilitar mapa y busqueda de lugares.
  }

  private resetDestinationState() {
    this.destination = null;
    this.destinationChange.emit(null);

    // si quieres: limpiar lugares seleccionados al cambiar de ciudad
    //LIMPIAR LOS LUGARES SELECCIONADOS PREVIAMENTE.

    // quitar restricción a autocomplete de lugares
    // this.placeCtrl.setValue('', { emitEvent: false });
    // this.placeCtrl.disable({ emitEvent: false });

    // opcional: token nuevo de sesión (buena práctica)
    // this.places.endSession();
    // this.places.newSessionToken();
  }
}
