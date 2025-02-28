
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet } from '@angular/router';
import { AuthService } from './app/services/auth.service';
import { AdminNavbarComponent } from './app/admin-navbar/admin-navbar.component';
import { NavbarComponent } from './app/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, AdminNavbarComponent, NavbarComponent],
  template: `
    <ng-container *ngIf="!isAuthPage">
      <ng-container *ngIf="isAdmin; else userNavbar">
        <app-admin-navbar></app-admin-navbar>
      </ng-container>
      <ng-template #userNavbar>
        <app-navbar></app-navbar>
      </ng-template>
    </ng-container>

    <div class="content-container">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .content-container {
      padding: 20px;
      max-width: 100%;
      min-height: calc(100vh - 64px);
    }
  `]
})
export class AppComponent implements OnInit, OnDestroy {
  isAdmin: boolean = false;
  isAuthPage: boolean = false;
  
  // Create a Subject that will be used to unsubscribe
  private destroy$ = new Subject<void>();

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // Check initial route
    this.checkIfAuthPage(this.router.url);
    
    // Listen to route changes
    this.router.events
      .pipe(
        takeUntil(this.destroy$), // Use takeUntil for automatic unsubscription
        filter(event => event instanceof NavigationEnd)
      )
      .subscribe((event: NavigationEnd) => {
        this.checkIfAuthPage(event.url);
      });

    // Listen for role changes from the auth service
    // IMPORTANT FIX: Using takeUntil to properly manage subscription
    // Added proper type to role parameter
    this.authService.role$
      .pipe(takeUntil(this.destroy$))
      .subscribe((role: string | null) => {
        console.log('Role changed:', role);
        this.isAdmin = role === '0';
      });
  }
  
  ngOnDestroy(): void {
    // Signal all subscriptions to complete when component is destroyed
    this.destroy$.next();
    this.destroy$.complete();
    console.log('AppComponent destroyed, all subscriptions cleaned up');
  }
  
  private checkIfAuthPage(url: string): void {
    const authPages = ['/login', '/signup', '/register'];
    this.isAuthPage = authPages.some(page => url.includes(page));
  }
}