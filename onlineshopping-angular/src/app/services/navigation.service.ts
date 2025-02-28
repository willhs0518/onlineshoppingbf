import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  constructor(private router: Router) {}

  /**
   * Navigate using page reload to ensure clean state
   * This bypasses Angular's routing to prevent potential freezing
   */
  navigateWithReload(path: string): void {
    // Use direct browser navigation instead of Angular router
    window.location.href = path;
  }

  /**
   * Standard Angular navigation for less critical paths
   */
  navigate(path: string): void {
    this.router.navigate([path]);
  }
}