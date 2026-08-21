import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { GoogleMap, GoogleMapsModule } from '@angular/google-maps';
import { MatListModule } from '@angular/material/list';
import { GooglePlacesService } from '../../services/google-places.service';
import { TripDestination } from '../../models/interfaces/TripDestination.interface';
import { PlaceItem } from '../../models/interfaces/PlaceItem.interface';

@Component({
  selector: 'app-mapa',
  standalone: true,
  imports: [
    CommonModule,
    GoogleMapsModule,
    MatCardModule,
    MatButtonModule,
    MatListModule,
    MatIconModule,
  ],
  templateUrl: './mapa.component.html',
  styleUrl: './mapa.component.css',
})
export class MapaComponent {
  @Input({ required: true }) destination: TripDestination | null = null;
  @Input({ required: true }) selectedPlaces: PlaceItem[] = [];

  // Opcional: filtra qué tipo de lugares sugerir en nearby search
  @Input() nearbyPrimaryTypes: string[] = ['tourist_attraction', 'restaurant', 'museum'];

  @Output() placeAdded = new EventEmitter<PlaceItem>();

  @ViewChild(GoogleMap) map?: GoogleMap;

  // Estado UI
  loading = false;
  error: string | null = null;

  nearbyResults: Array<{
    id: string;
    name: string;
    address?: string;
    lat: number;
    lng: number;
    primaryType?: string;
  }> = [];
  lastClickedLatLng?: google.maps.LatLngLiteral;

  // Config mapa
  zoom = 13;
  options: google.maps.MapOptions = {
    mapTypeControl: false,
    streetViewControl: false,
    fullscreenControl: false,
    clickableIcons: true, // necesario para clicks en POIs
  };

  constructor(private places: GooglePlacesService) {}

  ngOnChanges(): void {
    // Cuando cambia destino, ajusta el mapa a sus bounds
    if (this.destination && this.map?.googleMap) {
      const b = this.destination.bounds;
      const bounds = new google.maps.LatLngBounds(
        new google.maps.LatLng(b!.south, b!.west),
        new google.maps.LatLng(b!.north, b!.east),
      );
      this.map.googleMap.fitBounds(bounds);
    }
  }

  async onMapClick(ev: google.maps.MapMouseEvent) {
    this.error = null;

    // Si el click fue sobre un POI, Google incluye placeId y el ejemplo usa stop() para evitar el popup default [1](https://developers.google.com/maps/documentation/javascript/examples/event-poi)
    const placeId = (ev as any).placeId as string | undefined;

    if (placeId) {
      (ev as any).stop?.(); // previene el info window default (según patrón oficial) [1](https://developers.google.com/maps/documentation/javascript/examples/event-poi)
      await this.addFromPlaceId(placeId);
      return;
    }

    // Click en “vacío”: hacemos nearby search alrededor de la coordenada
    if (!ev.latLng) return;
    const latLng = { lat: ev.latLng.lat(), lng: ev.latLng.lng() };
    this.lastClickedLatLng = latLng;

    await this.loadNearby(latLng);
  }

  private async addFromPlaceId(placeId: string) {
    try {
      this.loading = true;

      const details = await this.places.fetchPlaceDetailsById(placeId);

      if (!details.lat || !details.lng) throw new Error('No hay coordenadas para este lugar.');

      const item: PlaceItem = {
        id: details.id,
        name: details.name ?? 'Lugar',
        address: details.address ?? '',
        category: this.mapPrimaryTypeToCategory(details.primaryType),
        icon: this.iconForCategory(this.mapPrimaryTypeToCategory(details.primaryType)),
        lat: details.lat,
        lng: details.lng,
      };

      this.placeAdded.emit(item);
    } catch (e: any) {
      this.error = 'No pude obtener los detalles del lugar. Revisa consola / API key.';
    } finally {
      this.loading = false;
    }
  }

  private async loadNearby(center: { lat: number; lng: number }) {
    try {
      this.loading = true;

      // Nearby Search New con Place.searchNearby() [3](https://developers.google.com/maps/documentation/javascript/reference/place)[2](https://developers.google.com/maps/documentation/javascript/examples/place-nearby-search)
      const results = await this.places.searchNearby({
        center,
        radiusMeters: 800, // ajustable (500–1500m)
        includedPrimaryTypes: this.nearbyPrimaryTypes.slice(0, 5),
        maxResultCount: 8,
      });

      this.nearbyResults = results;
    } catch {
      this.error = 'No se pudo cargar búsqueda cercana.';
      this.nearbyResults = [];
    } finally {
      this.loading = false;
    }
  }

  async addNearbyResult(r: any) {
    const item: PlaceItem = {
      id: r.id,
      name: r.name,
      address: r.address ?? '',
      category: this.mapPrimaryTypeToCategory(r.primaryType),
      icon: this.iconForCategory(this.mapPrimaryTypeToCategory(r.primaryType)),
      lat: r.lat,
      lng: r.lng,
    };

    this.placeAdded.emit(item);
  }

  // Helpers categoría/ícono
  private mapPrimaryTypeToCategory(primaryType?: string): string {
    if (!primaryType) return 'Cultura';
    if (primaryType.includes('restaurant') || primaryType.includes('food')) return 'Comida';
    if (primaryType.includes('cafe')) return 'Café';
    if (primaryType.includes('park') || primaryType.includes('natural')) return 'Naturaleza';
    if (primaryType.includes('museum') || primaryType.includes('tourist')) return 'Cultura';
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
}
