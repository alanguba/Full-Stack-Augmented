import { HttpClient, HttpResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ResetPasswordService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  firstStepResetPassword(email: string): Observable<HttpResponse<unknown>> {
    return this.http.post<unknown>(
      `${this.baseUrl}/api/auth/reset-password`,
      { email, otp: null },
      {
        observe: 'response',
      },
    );
  }

  resetPasswrod(email: string, password: string, code: string): Observable<HttpResponse<unknown>> {
    const body = {
      email: email,
      newPassword:password,
      otp: code,
    };

    return this.http.post<unknown>(`${this.baseUrl}/api/auth/reset-password`, body, {
      observe: 'response',
    });
  }
}
