import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import {
  Observable,
  of,
} from 'rxjs';
import { GooglePlacesService, PlaceSuggestionVM } from '../../services/google-places.service';
import { PlaceCategory } from '../../models/types/PlaceCategory.type';
import { PlaceItem } from '../../models/interfaces/PlaceItem.interface';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { TripDestination } from '../../models/interfaces/TripDestination.interface';

@Component({
  selector: 'app-busca-lugares-input',
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
  ],
  templateUrl: './busca-lugares-input.component.html',
  styleUrl: './busca-lugares-input.component.css',
})
export class BuscaLugaresInputComponent {
  includedPrimaryTypes: string[] = []; // ejemplo: ['restaurant', 'tourist_attraction']
  categories: PlaceCategory[] = ['Cultura', 'Naturaleza', 'Comida', 'Aventura', 'Café', 'Compras'];
  selectedCategory: PlaceCategory | 'Todas' = 'Todas';
  
  @Input() destination: TripDestination | null = null; // recibe destino para usar sus bounds en la búsqueda
  @Input() placeCtrl!: FormControl<string>;
  @Input() placeSuggestions$!: Observable<PlaceSuggestionVM[]>;
  @Input() loadingSuggestions!: boolean;
  @Input() placesError!: string | null;
  @Input() displaySuggestion!: (s: PlaceSuggestionVM) => string;

  @Output() placeSelected = new EventEmitter<PlaceItem>();


  constructor(private places: GooglePlacesService) {}

  private mapPrimaryTypeToCategory(primaryType?: string): any {
    // mapeo simple (ajusta a tu gusto)
    if (!primaryType) return 'Cultura';
    if (primaryType.includes('restaurant') || primaryType.includes('food')) return 'Comida';
    if (primaryType.includes('cafe')) return 'Café';
    if (primaryType.includes('park') || primaryType.includes('natural')) return 'Naturaleza';
    if (primaryType.includes('tourist') || primaryType.includes('museum')) return 'Cultura';
    return 'Cultura';
  }

  private iconForCategory(cat: string): string {
    switch (cat) {
      case 'Comida':
        return 'restaurant';
      case 'Café':
        return 'local_cafe';
      case 'Naturaleza':
        return 'park';
      case 'Aventura':
        return 'terrain';
      case 'Compras':
        return 'shopping_bag';
      default:
        return 'place';
    }
  }

  async onSuggestionSelected(s: PlaceSuggestionVM) {
    if (!s?.prediction) return;

    const details = await this.places.fetchPlaceDetails(s.prediction);

    // Convierte a tu modelo PlaceItem (como el mock, pero real)
    const placeItem: PlaceItem = {
      id: s.placeId ?? crypto.randomUUID(),
      name: details.name ?? s.text,
      category: this.mapPrimaryTypeToCategory(details.primaryType),
      address: details.address ?? '',
      rating: undefined,
      icon: this.iconForCategory(this.mapPrimaryTypeToCategory(details.primaryType)),
      lat: details.lat ?? 0,
      lng: details.lng ?? 0,
    };

    // Reutiliza tu método existente
    this.placeSelected.emit(placeItem);

    // Limpia input para agregar otro
    this.placeCtrl.setValue('');

    // Opcional: cerrar sesión y crear una nueva para siguientes búsquedas
    // await this.places.endSession();
    // await this.places.newSessionToken();
  }

}
