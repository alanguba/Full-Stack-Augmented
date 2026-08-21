import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenCacheService {
  private readonly tokenKey = 'auth_jwt';

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  clear(): void {
    localStorage.removeItem(this.tokenKey);
  }

  isTokenExpired(token: string): boolean {
    try {
      const payload = this.decodePayload(token);
      if (!payload?.exp) {
        return true;
      }

      const nowInSeconds = Math.floor(Date.now() / 1000);
      return payload.exp <= nowInSeconds;
    } catch {
      return true;
    }
  }

  hasAnyRole(expectedRoles: string[]): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    const payload = this.decodePayload(token);
    const currentRole = payload.role;
    return expectedRoles.some((role) => currentRole === role);
  }

  getTokenExpirationDate(token: string): Date | null {
    try {
      const payload = this.decodePayload(token);
      if (!payload?.exp) {
        return null;
      }

      return new Date(payload.exp * 1000);
    } catch {
      return null;
    }
  }

  getIdFromToken(token: string): string {
    try {
      const payload = this.decodePayload(token);
      if (!payload?.id) {
        return '';
      }

      return String(payload.id);
    } catch {
      return '';
    }
  }

  private decodePayload(token: string): any {
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('Invalid JWT format');
    }

    const payload = parts[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded);
  }
}
