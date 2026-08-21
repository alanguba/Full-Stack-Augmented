import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TripPlanStore } from '../../services/trip-plan.service';

export const hasPlanGuard: CanActivateFn = () => {
  const store = inject(TripPlanStore);
  const router = inject(Router);

  if (store.getSnapshot()) return true;

  router.navigateByUrl('/planes');
  return false;
};