import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080';

  firstStepLogin(email: string, password: string): Observable<HttpResponse<unknown>> {
    const headers = new HttpHeaders({
      username: email,
      password
    });

    return this.http.post<unknown>(`${this.baseUrl}/login`, {}, {
      headers,
      observe: 'response'
    });
  }

  verifyOtp(email: string, password: string, code: string): Observable<HttpResponse<unknown>> {
    const headers = new HttpHeaders({
      username: email,
      password,
      code
    });

    return this.http.post<unknown>(`${this.baseUrl}/login`, {}, {
      headers,
      observe: 'response'
    });
  }
}
