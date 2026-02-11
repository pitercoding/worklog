import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';
import { LoadingStateService } from './loading-state.services';

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loadingState = inject(LoadingStateService);
  loadingState.show();

  return next(req).pipe(finalize(() => loadingState.hide()));
};
