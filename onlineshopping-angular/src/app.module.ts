import { bootstrapApplication } from '@angular/platform-browser';
import { importProvidersFrom } from '@angular/core';
import { AppComponent } from './app.component';
import { provideRouter, Routes } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuthInterceptor } from './app/interceptors/auth.interceptors';

// Define your routes
const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./app/login/login.component').then(m => m.LoginComponent) },
  { path: 'signup', loadComponent: () => import('./app/signup/signup.component').then(m => m.SignupComponent) },
  { path: 'home', loadComponent: () => import('./app/userhome/userhome.component').then(m => m.UserHomeComponent) },
  { path: 'admin', loadComponent: () => import('./app/admin-home/admin-home.component').then(m => m.AdminHomeComponent) },
  { path: 'order-detail/:id', loadComponent: () => import('./app/order-detail/order-detail.component').then(m => m.OrderDetailComponent) },
  { path: 'product-detail/:id', loadComponent: () => import('./app/product-detail/product-detail.component').then(m => m.ProductDetailComponent) },
  { path: 'products', loadComponent: () => import('./app/product-list/product-list.component').then(m => m.ProductListComponent) },
  { path: 'cart', loadComponent: () => import('./app/shopping-cart/shopping-cart.component').then(m => m.ShoppingCartComponent) },
  { path: 'watchlist', loadComponent: () => import('./app/watchlist/watchlist.component').then(m => m.WatchlistComponent) },
  { path: 'admin/products', loadComponent: () => import('./app/admin-product-list/admin-product-list.component').then(m => m.AdminProductListComponent) },
  { path: 'admin/products/edit/:idt', loadComponent: () => import('./app/admin-product-edit/admin-product-edit.component').then(m => m.AdminProductEditComponent) },
  { path: 'admin/products/add', loadComponent: () => import('./app/admin-product-edit/admin-product-edit.component').then(m => m.AdminProductEditComponent) }
];

// Bootstrap the application
bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    importProvidersFrom(
      BrowserAnimationsModule,
      FormsModule,
      ReactiveFormsModule,
      HttpClientModule
    ),
    provideHttpClient(withInterceptors([AuthInterceptor]))
  ]
}).catch(err => console.error(err));