import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { PlanResponse } from '../../models/interfaces/Plan.interface';
import { Router } from '@angular/router';


@Component({
  selector: 'app-destino-card',
  imports: [MatCardModule,MatIconModule, CommonModule, MatButtonModule],
  templateUrl: './destino-card.component.html',
  styleUrl: './destino-card.component.css',
})
export class DestinoCardComponent {
  @Input({required: true}) item: PlanResponse = {} as PlanResponse;

  constructor(private router: Router) {}

  paceLabelClass(pace: PlanResponse['ritmo']): string {
    switch (pace) {
      case 'Relajado':
        return 'pace-relaxed';
      case 'Balanceado':
        return 'pace-balanced';
      case 'Intenso':
        return 'pace-intense';
      default:
        return '';
    }
  }

  goToItinerario(id: number) {
    this.router.navigate(['/home/itinerario', id]);
  }
}
