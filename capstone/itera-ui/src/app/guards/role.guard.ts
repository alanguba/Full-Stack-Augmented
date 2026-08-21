import { inject } from '@angular/core';
import { CanActivateFn, ActivatedRouteSnapshot, Router } from '@angular/router';
import { TokenCacheService } from '../services/token-cache.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const tokenCache = inject(TokenCacheService);
  const router = inject(Router);

  const expectedRoles = route.data['roles'] as string[] | undefined;

  if (expectedRoles && expectedRoles.length !== 0) {
    if (tokenCache.hasAnyRole(expectedRoles)) {
      return true;
    }
  }

  return router.createUrlTree(['/home/inicio'], {
    queryParams: { reason: 'forbidden' },
  });
};
