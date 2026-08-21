import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PlanByIdResponse } from '../../models/interfaces/PlanRequest.interface';
import { ItineraryApiService } from '../../services/itinerary-api.service';

@Component({
  selector: 'app-itinerario-detalle.componente',
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule,
    MatListModule,
    MatButtonModule,
  ],
  templateUrl: './itinerario-detalle.componente.html',
  styleUrl: './itinerario-detalle.componente.css',
})
export class ItinerarioDetalleComponente {
  private route = inject(ActivatedRoute)
  plan: PlanByIdResponse | null = null;
  isLoading = false;
  error: string | null = null;
  itineraryId!: string;

  readonly coverImage =
    'https://images.unsplash.com/photo-1519985176271-adb1088fa94c?q=80&w=1400&auto=format&fit=crop';

  constructor(private router: Router, private api: ItineraryApiService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.error = null;
    this.plan = null;

    this.itineraryId = this.route.snapshot.paramMap.get('id')!;

    this.api.getPlanById(this.itineraryId).subscribe({
      next: (res) => {
        this.plan = res;
        this.isLoading = false;
      },
      error: (err) => {
        // Show a friendly message
        this.error = err?.error?.error ?? 'No se pudo generar el itinerario.';
        this.isLoading = false;
      },
    });
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = 'assets/mock/default-destination.jpg';
  }

  goToItinerariosLista(): void {
    this.router.navigate(['/home/mis-itinerarios']);
  }
}
