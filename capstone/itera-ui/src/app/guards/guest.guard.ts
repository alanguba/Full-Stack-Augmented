import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenCacheService } from '../services/token-cache.service';

export const guestGuard: CanActivateFn = () => {
  const tokenCache = inject(TokenCacheService);
  const router = inject(Router);

  if (tokenCache.isAuthenticated()) {
    return router.createUrlTree(['/home/inicio']);
  }

  return true;
};
