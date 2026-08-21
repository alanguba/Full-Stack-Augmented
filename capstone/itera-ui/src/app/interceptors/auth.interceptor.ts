import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenCacheService } from '../services/token-cache.service';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenCache = inject(TokenCacheService);
  const router = inject(Router);

  const token = tokenCache.getToken();

  if (!token) {
    return next(req);
  }

  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `${token}`,
        },
      })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        tokenCache.clear();
        router.navigate(['/login'], {
          queryParams: { reason: 'expired' },
        });
      }

      return throwError(() => error);
    }),
  );
};
