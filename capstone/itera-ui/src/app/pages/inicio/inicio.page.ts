import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { Router, RouterModule } from '@angular/router';


@Component({
  selector: 'app-inicio',
  imports: [
    RouterModule,
    MatButtonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule,
    MatListModule,
  ],
  templateUrl: './inicio.page.html',
  styleUrl: './inicio.page.css',
  standalone: true,
})
export class InicioPage {
  currentYear = new Date().getFullYear();

  constructor(private router: Router) {}

  goToPlanes(): void {
    this.router.navigateByUrl('/home/planes');
  }
}
