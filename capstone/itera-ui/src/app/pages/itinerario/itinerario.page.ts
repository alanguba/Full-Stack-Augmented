import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { PlaceItem } from '../../models/interfaces/PlaceItem.interface';
import { TripPlanInput } from '../../models/interfaces/TripPlanInput.interface';
import { Subscription } from 'rxjs';
import { TripPlanStore } from '../../services/trip-plan.service';
import { ItineraryRequest } from '../../models/interfaces/ItineraryRequest.interface';
import { ItineraryResponse } from '../../models/interfaces/ItineraryResponse.interface';
import { ItineraryApiService } from '../../services/itinerary-api.service';
import { PlanRequest } from '../../models/interfaces/PlanRequest.interface';
import { TokenCacheService } from '../../services/token-cache.service';

@Component({
  selector: 'app-itinerario',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatDividerModule,
    MatListModule,
    MatProgressBarModule,
  ],
  templateUrl: './itinerario.page.html',
  styleUrls: ['./itinerario.page.css'],
})
export class ItinerarioPage implements OnInit {
  // Estado loading mientras “IA genera”

  isLoading = false;
  error: string | null = null;

  planInput!: ItineraryRequest; // request payload
  result: ItineraryResponse | null = null;

  constructor(
    private router: Router,
    private store: TripPlanStore,
    private api: ItineraryApiService,
    private tokenCache: TokenCacheService
  ) {}

  ngOnInit(): void {
    const plan = this.store.getSnapshot();
    if (!plan) {
      this.error = 'No hay datos del plan. Regresa a Planes.';
      return;
    }

    this.planInput = {
      destination: plan.destination.label, // <-- adjust depending on your backend DTO
      days: plan.days,
      pace: plan.pace,
      places: plan.places.map((p) => ({
        id: p.id,
        name: p.name,
        address: p.address,
        category: p.category,
      })),
    };

    this.generate();
  }

  generate(): void {
    this.isLoading = true;
    this.error = null;
    this.result = null;

    this.api.generateItinerary(this.planInput).subscribe({
      next: (res) => {
        this.result = res;
        console.log(this.result);
        this.isLoading = false;
      },
      error: (err) => {
        // Show a friendly message
        this.error = err?.error?.error ?? 'No se pudo generar el itinerario.';
        this.isLoading = false;
      },
    });
  }

  savePlan(): void {
    let planToBeSaved: PlanRequest;
    if (!this.result) {
      // Si no hay resultado, no se puede guardar
      this.error = 'No hay itinerario generado para guardar.';
      return;
    }
    const userId = this.tokenCache.getIdFromToken(this.tokenCache.getToken() ?? '');
    planToBeSaved = {
      name: `Plan para ${this.result.destination} - ${new Date().toLocaleDateString()}`,
      destination: this.result.destination,
      usuario_id: +userId,
      days: this.result.days,
      pace: this.result.pace,
      lugares: [],
      itinerary: [],
    };

    planToBeSaved.lugares = this.planInput.places.map((place) => ({
      name: place.name,
      direction: place.address!,
      category: place.category!,
    }));

    planToBeSaved.itinerary = this.result.itinerary.map((day) => ({
      day: day.day,
      title: day.title,
      summary: day.summary,
      stops: day.stops.map((stop) => ({
        time: stop.time,
        name: stop.name,
        note: stop.note,
      })),
    }));

    console.log(this.getDestinationImage(this.planInput.destination));

    // Implementation for saving the plan
    this.api.saveItinerary(planToBeSaved).subscribe({
      next: () => {
        // Handle successful save
        console.log('Plan guardado exitosamente.');
        this.router.navigateByUrl('/home/mis-itinerarios');
      },
      error: (err) => {
        // Handle save error
        console.error('Error al guardar el plan.', err);
      },
    });
  }

  getDestinationImage(destination: string): string {
    if (!destination) return '';

    // Tomar solo la primera parte (ciudad)
    const city = destination.split(',')[0].trim().toLowerCase();

    return `https://source.unsplash.com/featured/?${encodeURIComponent(city)}`;
  }

  backToPlans(): void {
    this.router.navigateByUrl('/home/planes');
  }
}
