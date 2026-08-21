import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import {
  MatListItemIcon,
  MatListItemLine,
  MatListItemTitle,
  MatListModule,
} from '@angular/material/list';
import { PlaceItem } from '../../models/interfaces/PlaceItem.interface';
import { MatIconButton } from '@angular/material/button';

@Component({
  selector: 'app-lista-lugares',
  imports: [
    MatCardModule,
    MatIconModule,
    MatDividerModule,
    MatListModule,
    MatListItemIcon,
    MatListItemTitle,
    MatListItemLine,
    MatIconButton
  ],
  templateUrl: './lista-lugares.component.html',
  styleUrl: './lista-lugares.component.css',
})
export class ListaLugaresComponent {
  @Input({required: true}) selected: PlaceItem[] = [];
  @Output() selectedChange = new EventEmitter<PlaceItem[]>();

  removePlace(place: PlaceItem): void {
    this.selected = this.selected.filter((p) => p.id !== place.id);
    this.selectedChange.emit(this.selected);
  }
}
