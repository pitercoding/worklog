import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../ui/notification.services';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const isExpectedNoWorkday404 =
        error.status === 404 &&
        req.method === 'GET' &&
        req.url.includes('/api/worklog/');

      if (isExpectedNoWorkday404) {
        return throwError(() => error);
      }

      const backendMessage = error.error?.message;
      const message =
        typeof backendMessage === 'string' && backendMessage.length > 0
          ? backendMessage
          : error.message || 'Unexpected API error';

      console.error('API Error', {
        url: req.url,
        status: error.status,
        message,
      });

      notificationService.error(message);

      return throwError(() => error);
    })
  );
};
