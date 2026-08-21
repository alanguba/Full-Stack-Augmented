import { Component, Input, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatSidenav, MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { TokenCacheService } from '../../services/token-cache.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatInputModule,
    MatFormFieldModule,
    MatSidenavModule,
    MatListModule,
  ],
})
export class HeaderComponent {
  @Input() title = 'Itera';
  @Input() enableSidenav = true;

  @ViewChild('sidenav') sidenav?: MatSidenav;

  searchText = '';
  sidenavOpened = false;

  constructor(private router: Router, private TokenCache: TokenCacheService) {}

  goHome(): void {
    this.router.navigateByUrl('/inicio');
  }

  goTo(url: string): void {
    this.router.navigateByUrl(url);
  }

  logout(): void {
    this.TokenCache.clear();
    this.router.navigateByUrl('/login');
  }

  hasRole(role: string): boolean {
    return this.TokenCache.hasAnyRole([role]);
  }

  toggleSidenav(): void {
    if (!this.enableSidenav) return;
    this.sidenavOpened = !this.sidenavOpened;
  }

  closeSidenav(): void {
    this.sidenavOpened = false;
  }
}
