import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error) => {
      if (error.status === 404) {
        return throwError(() => error);
      }

      let message: string;
      if (error.status === 400) {
        message = error.error?.message || 'Bad request';
      } else if (error.status === 409) {
        message = error.error?.message || 'Conflict error';
      } else if (error.status === 401 || error.status === 403) {
        message = 'Unauthorized';
      } else if (error.status >= 500) {
        message = 'Server error occurred';
      } else {
        message = 'An unexpected error occurred';
      }

      snackBar.open(message, 'Close', { duration: 5000 });

      return throwError(() => error);
    }),
  );
};
