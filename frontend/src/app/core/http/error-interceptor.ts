import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
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

      if (typeof window !== 'undefined') {
        window.alert(message);
      }

      return throwError(() => error);
    })
  );
};
