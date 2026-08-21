import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, tap, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { BackendStatusService } from '../services/backend-status.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);
  const backendStatusService = inject(BackendStatusService);

  return next(req).pipe(
    tap(() => {
      // if a request succeeds, mark backend as up again
      backendStatusService.markBackendUp();
    }),
    catchError((error: HttpErrorResponse) => {
      if (error.status === 0 || error.status >= 500) {
        const shouldShow = backendStatusService.markBackendDown();

        if (shouldShow) {
          toastService.error('No es posible conectarse con el servidor. Por favor, inténtelo de nuevo más tarde.');
        }
      }

      return throwError(() => error);
    })
  );
};
