import { Routes } from '@angular/router';
import { LoginComponent } from './app/login/login.component';
import { SignupComponent } from './app/signup/signup.component';
import { UserHomeComponent } from './app/userhome/userhome.component';
import { OrderDetailComponent } from './app/order-detail/order-detail.component';
import { ProductDetailComponent } from './app/product-detail/product-detail.component';
import { ProductListComponent } from './app/product-list/product-list.component';
import { ShoppingCartComponent } from './app/shopping-cart/shopping-cart.component';
import { WatchlistComponent } from './app/watchlist/watchlist.component';
import { AdminHomeComponent } from './app/admin-home/admin-home.component';
import { AdminProductListComponent } from './app/admin-product-list/admin-product-list.component';
import { AdminProductEditComponent } from './app/admin-product-edit/admin-product-edit.component';
import { AuthGuard } from './auth.guard'; 

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'home', component: UserHomeComponent, canActivate: [AuthGuard] },
  { path: 'admin', component: AdminHomeComponent, canActivate: [AuthGuard]},
  { path: 'order-detail/:id', component: OrderDetailComponent, canActivate: [AuthGuard] }, 
  { path: 'product-detail/:id', component: ProductDetailComponent }, 
  { path: 'products', component: ProductListComponent }, 
  { path: 'cart', component: ShoppingCartComponent }, 
  { path: 'watchlist', component: WatchlistComponent },
  { path: 'admin/products', component: AdminProductListComponent },
  { path: 'admin/products/edit/:id', component: AdminProductEditComponent },
  { path: 'admin/products/add', component: AdminProductEditComponent }
];