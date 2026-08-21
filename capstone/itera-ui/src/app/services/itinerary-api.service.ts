
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ItineraryRequest } from '../models/interfaces/ItineraryRequest.interface';
import { ItineraryResponse } from '../models/interfaces/ItineraryResponse.interface';
import { PlanByIdResponse, PlanRequest } from '../models/interfaces/PlanRequest.interface';
import { PlanSaveResponse } from '../models/interfaces/PlanSaveResponse.interface';
import { PlanResponse } from '../models/interfaces/Plan.interface';

@Injectable({ providedIn: 'root' })
export class ItineraryApiService {

  private readonly url = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  generateItinerary(payload: ItineraryRequest): Observable<ItineraryResponse> {
    return this.http.post<ItineraryResponse>(`${this.url}/itinerario/generate`, payload);
  }

  saveItinerary(plan: PlanRequest): Observable<PlanSaveResponse> {
    return this.http.post<PlanSaveResponse>(`${this.url}/plan/save`, plan);
  }

  getAllPlans(userId: string): Observable<PlanResponse[]> {
    return this.http.get<PlanResponse[]>(`${this.url}/plan?userId=${userId}`);
  }

  getPlanById(planId: string): Observable<PlanByIdResponse> {
    return this.http.get<PlanByIdResponse>(`${this.url}/plan/get?planId=${planId}`);
  }
}
