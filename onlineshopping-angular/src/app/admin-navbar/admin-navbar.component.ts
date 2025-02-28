import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';

@Component({
  selector: 'app-admin-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive
  ],
  template: `
    <nav class="navbar">
      <div class="brand">Admin Panel</div>
      <div class="nav-links">
        <a routerLink="/admin" routerLinkActive="active">Dashboard</a>
        <a routerLink="/admin/products" routerLinkActive="active">Manage Products</a>
        <button (click)="logout()" class="logout-btn">Logout</button>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.5rem 2rem;
      background-color: white;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .brand {
      font-size: 1.25rem;
      font-weight: bold;
    }
    .nav-links {
      display: flex;
      gap: 1.5rem;
    }
    .nav-links a {
      color: #333;
      text-decoration: none;
      padding: 0.5rem 0;
    }
    .nav-links a:hover {
      color: #000;
    }
    .active {
      font-weight: bold;
      border-bottom: 2px solid #333;
    }
  `]
})
export class AdminNavbarComponent {
  constructor(private router: Router) {}

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}

