// import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
// import { inject } from '@angular/core';
// import { Observable, throwError } from 'rxjs';
// import { catchError } from 'rxjs/operators';
// import { AuthService } from '../services/auth.service';
// import { Router } from '@angular/router';

// export const AuthInterceptor: HttpInterceptorFn = (
//   req: HttpRequest<unknown>,
//   next: HttpHandlerFn
// ): Observable<HttpEvent<unknown>> => {
//   const authService = inject(AuthService);
//   const router = inject(Router);
  
//   // Get the token from the auth service
//   const token = authService.getToken();

//   // If token exists, add it to the request headers
//   if (token) {
//     req = req.clone({
//       setHeaders: {
//         Authorization: `Bearer ${token}`
//       }
//     });
//   }

//   // Send the modified request
//   return next(req).pipe(
//     catchError((error: HttpErrorResponse) => {
//       // Handle 401 Unauthorized errors (token expired or invalid)
//       if (error.status === 401) {
//         console.warn('Unauthorized - Redirecting to login'); //  Debug log
//         // Clear local storage
//         localStorage.removeItem('token');
//         localStorage.removeItem('role');
        
//         // Redirect to login page
//         router.navigate(['/login']);
//       }
      
//       return throwError(() => error);
//     })
//   );
// };

import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

// Track active requests to prevent memory leaks
let activeRequests = 0;

export const AuthInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  // Get the token from the auth service
  const token = authService.getToken();

  // Track active request count
  activeRequests++;
  
  // Clone the request instead of modifying the original
  let clonedReq = req;
  
  // If token exists, add it to the request headers
  if (token) {
    clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // Send the modified request
  return next(clonedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Handle 401 Unauthorized errors (token expired or invalid)
      if (error.status === 401) {
        console.warn('Unauthorized - Redirecting to login');
        
        // Let the AuthService handle logout to centralize logic
        authService.logout();
      }
      
      return throwError(() => error);
    }),
    // Always run finalize to decrement active requests
    finalize(() => {
      activeRequests--;
      console.log(`Active requests: ${activeRequests}`);
    })
  );
};