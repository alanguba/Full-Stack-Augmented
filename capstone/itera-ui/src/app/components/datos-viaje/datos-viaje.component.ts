import { Component, Input } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { PlaceItem } from '../../models/interfaces/PlaceItem.interface';

@Component({
  selector: 'app-datos-viaje',
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonToggleModule,
    MatIcon,
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './datos-viaje.component.html',
  styleUrl: './datos-viaje.component.css',
})
export class DatosViajeComponent {
  // Datos del viaje
  daysOptions = Array.from({ length: 10 }, (_, i) => i + 1);

  @Input({ required: true })
  daysControl!: FormControl<number>;

  @Input({ required: true })
  paceControl!: FormControl<'Relajado' | 'Balanceado' | 'Intenso'>;

  @Input({ required: true }) selected: PlaceItem[] = [];
}
