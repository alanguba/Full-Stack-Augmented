import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { TripPlanInput } from '../models/interfaces/TripPlanInput.interface';

@Injectable({ providedIn: 'root' })
export class TripPlanStore {
  private readonly KEY = 'itera_trip_plan';
  private readonly _plan$ = new BehaviorSubject<TripPlanInput | null>(this.load());

  readonly plan$ = this._plan$.asObservable();

  setPlan(plan: TripPlanInput): void {
    this._plan$.next(plan);
    localStorage.setItem(this.KEY, JSON.stringify(plan));
  }

  getSnapshot(): TripPlanInput | null {
    return this._plan$.value;
  }

  clear(): void {
    this._plan$.next(null);
    localStorage.removeItem(this.KEY);
  }

  private load(): TripPlanInput | null {
    try {
      const raw = localStorage.getItem(this.KEY);
      return raw ? (JSON.parse(raw) as TripPlanInput) : null;
    } catch {
      return null;
    }
  }
}
