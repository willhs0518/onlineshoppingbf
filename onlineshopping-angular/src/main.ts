// import { bootstrapApplication } from '@angular/platform-browser';
// import { AppComponent } from './app.component';
// import { provideRouter } from '@angular/router';
// import { provideHttpClient, withInterceptors } from '@angular/common/http';
// import { importProvidersFrom } from '@angular/core';
// import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// import { routes } from './app.route';
// import { AuthInterceptor } from './app/interceptors/auth.interceptors';

// bootstrapApplication(AppComponent, {
//   providers: [
//     provideRouter(routes),
//     provideHttpClient(withInterceptors([AuthInterceptor])),
//     importProvidersFrom(BrowserAnimationsModule)
//   ]
// })
// .catch(err => console.error(err));
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { routes } from './app.route';
import { AuthInterceptor } from './app/interceptors/auth.interceptors';

// Clear any potential stale state related to admin login
if (window.location.pathname === '/admin') {
  const role = localStorage.getItem('role');
  if (role !== '0') {
    // If on admin page but not admin role, redirect to login
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    window.location.href = '/login';
  }
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([AuthInterceptor])),
    importProvidersFrom(BrowserAnimationsModule)
  ]
})
.catch(err => console.error(err));