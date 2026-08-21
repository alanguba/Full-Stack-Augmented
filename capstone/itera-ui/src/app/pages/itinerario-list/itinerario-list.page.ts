import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { Subscription } from 'rxjs';
import { DestinoCardComponent } from '../../components/destino-card/destino-card.component';
import { ItineraryApiService } from '../../services/itinerary-api.service';
import { PlanResponse } from '../../models/interfaces/Plan.interface';
import { Router } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TokenCacheService } from '../../services/token-cache.service';

interface ItineraryItem {
  id: string;
  title: string;
  destination: string;
  days: number;
  pace: 'Relajado' | 'Balanceado' | 'Intenso';
  createdAt: string;
  placesCount: number;
  coverImage?: string;
  summary: string;
}

@Component({
  selector: 'app-itinerario-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    DestinoCardComponent,
    MatProgressSpinnerModule
  ],
  templateUrl: './itinerario-list.page.html',
  styleUrls: ['./itinerario-list.page.css'],
})
export class ItinerarioListPage implements OnInit, OnDestroy {
  searchCtrl = new FormControl('', { nonNullable: true });

  pageSize = 6;
  pageIndex = 0;

  private searchSub?: Subscription;

  isLoading = false;
  error: string | null = null;

  itineraries: PlanResponse[] | null = null;

  constructor(
    private api: ItineraryApiService,
    private router: Router,
    private tokenCache: TokenCacheService
  ) {}

  ngOnInit(): void {
    this.searchSub = this.searchCtrl.valueChanges.subscribe(() => {
      this.pageIndex = 0;
    });

    this.isLoading = true;
    this.error = null;
    this.itineraries = null;

    const userId = this.tokenCache.getIdFromToken(this.tokenCache.getToken() ?? '');

    this.api.getAllPlans(userId).subscribe({
      next: (res) => {
        this.itineraries = res;
        console.log(this.itineraries);
        this.isLoading = false;
      },
      error: (err) => {
        // Show a friendly message
        this.error = err?.error?.error ?? 'No se pudo generar el itinerario.';
        this.isLoading = false;
      },
    });
  }

  ngOnDestroy(): void {
    this.searchSub?.unsubscribe();
  }

  get filteredItineraries(): PlanResponse[] {
    const term = this.searchCtrl.value.trim().toLowerCase();

    if (!term) return this.itineraries!;

    return this.itineraries!.filter(
      (item) =>
        item.nombre.toLowerCase().includes(term) || item.destino.toLowerCase().includes(term),
    );
  }

  get paginatedItineraries(): PlanResponse[] {
    const start = this.pageIndex * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredItineraries.slice(start, end);
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  trackById(index: number, item: PlanResponse): string {
    return item.id.toString();
  }

  goTo(url: string): void {
    this.router.navigateByUrl(url);
  }
}
