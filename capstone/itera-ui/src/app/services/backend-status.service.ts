import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BackendStatusService {
  readonly backendDown = signal(false);

  markBackendDown(): boolean {
    if (this.backendDown()) {
      return false; // already marked, don't show again
    }

    this.backendDown.set(true);
    return true;
  }

  markBackendUp(): void {
    this.backendDown.set(false);
  }
}