import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenCacheService } from '../services/token-cache.service';

export const authGuard: CanActivateFn = () => {
  const tokenCache = inject(TokenCacheService);
  const router = inject(Router);

  const token = tokenCache.getToken();

  if (!token) {
    return router.createUrlTree(['/login'], {
      queryParams: { reason: 'missing-session' },
    });
  }

  if (tokenCache.isTokenExpired(token)) {
    tokenCache.clear();
    return router.createUrlTree(['/login'], {
      queryParams: { reason: 'expired' },
    });
  }
  return true;
};
