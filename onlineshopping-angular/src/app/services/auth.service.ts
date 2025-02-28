// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { tap } from 'rxjs/operators';
// import { BehaviorSubject } from 'rxjs';
// import { Router } from '@angular/router';


// @Injectable({
//   providedIn: 'root'
// })
// export class AuthService {
//   private apiUrl = 'http://localhost:8080'; // Base API URL - change this in production

//   private roleSubject = new BehaviorSubject<string | null>(localStorage.getItem('role')); // Reactive role
//   role$ = this.roleSubject.asObservable(); // Observable to track changes

//   constructor(private http: HttpClient, private router: Router) {
//     // Initialize the role from localStorage when service is created
//     const storedRole = localStorage.getItem('role');
//     if (storedRole) {
//       this.roleSubject.next(storedRole);
//     }
//   }

//   // Register a new user
//   register(user: any): Observable<any> {
//     return this.http.post(`${this.apiUrl}/signup`, user);
//   }

//   // Login user
//   // Login user
//   login(username: string, password: string): Observable<any> {
//     return this.http.post<any>(`${this.apiUrl}/login`, { username, password })
//       .pipe(
//         tap(response => {
//           if (response && response.token) {
//             // Store token and user info in localStorage
//             localStorage.setItem('token', response.token);
//             localStorage.setItem('role', response.role.toString());
            
//             // Update the role subject
//             this.roleSubject.next(response.role.toString());
            
//             // Navigate based on role
//             if (response.role === 0 || response.role === '0') {
//               this.router.navigate(['/admin']);
//             } else {
//               this.router.navigate(['/home']);
//             }
//           }
//         })
//       );
//   }


//   // Logout user
//   logout(): Observable<any> {
//     // Clear local storage
//     localStorage.removeItem('token');
//     localStorage.removeItem('role');
    
//     // Call the logout endpoint
//     return this.http.post<any>(`${this.apiUrl}/logout`, {});
//   }

//   // Check if user is logged in
//   isLoggedIn(): boolean {
//     return !!localStorage.getItem('token');
//   }

//   // Get user role
//   getUserRole(): number {
//     const role = localStorage.getItem('role');
//     return role ? parseInt(role) : -1;
//   }

//   // Check if user is admin
//   isAdmin(): boolean {
//     return this.getUserRole() === 0;
//   }

//   // Get authentication token
//   getToken(): string | null {
//     return localStorage.getItem('token');
//   }
// }
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080';
  
  // Create a BehaviorSubject to track the user's role
  private roleSubject = new BehaviorSubject<string | null>(localStorage.getItem('role'));
  
  // Expose the roleSubject as an observable
  role$ = this.roleSubject.asObservable();
  
  constructor(private http: HttpClient, private router: Router) {
    // Initialize role from localStorage
    const storedRole = localStorage.getItem('role');
    if (storedRole) {
      this.roleSubject.next(storedRole);
    }
  }
  
  // Register a new user
  register(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, user);
  }

  // Login user
  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { username, password })
      .pipe(
        tap(response => {
          if (response && response.token) {
            // Clear any existing auth data first
            this.clearAuthData();
            
            // Store token and user info
            localStorage.setItem('token', response.token);
            localStorage.setItem('role', response.role.toString());
            
            // Update the role subject
            this.roleSubject.next(response.role.toString());
            
            // Navigate based on role
            if (response.role === 0 || response.role === '0') {
              this.router.navigate(['/admin']);
            } else {
              this.router.navigate(['/home']);
            }
          }
        }),
        catchError(error => {
          console.error('Login error:', error);
          throw error;
        })
      );
  }

  // Logout user
  logout(): void {
    // Clear auth data
    this.clearAuthData();
    
    // Update the role subject
    this.roleSubject.next(null);
    
    // Navigate to login
    this.router.navigate(['/login']);
  }
  
  // Helper to clear authentication data
  private clearAuthData(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // Get user role
  getUserRole(): number {
    const role = localStorage.getItem('role');
    return role ? parseInt(role) : -1;
  }

  // Check if user is admin
  isAdmin(): boolean {
    return this.getUserRole() === 0;
  }

  // Get authentication token
  getToken(): string | null {
    return localStorage.getItem('token');
  }
}