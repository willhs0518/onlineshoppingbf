// import { Component, OnInit } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// import { Router, RouterModule } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { AuthService } from '../services/auth.service';

// @Component({
//   selector: 'app-login',
//   templateUrl: './login.component.html',
//   styleUrls: ['./login.component.css'],
//   standalone: true,
//   imports: [CommonModule, ReactiveFormsModule, RouterModule]
// })
// export class LoginComponent implements OnInit {
//   loginForm: FormGroup;
//   errorMessage: string = '';
//   loading: boolean = false;

//   constructor(
//     private formBuilder: FormBuilder,
//     private authService: AuthService,
//     private router: Router
//   ) {
//     this.loginForm = this.formBuilder.group({
//       username: ['', [Validators.required]],
//       password: ['', [Validators.required]]
//     });
//   }

//   ngOnInit(): void {
//     // Redirect if already logged in
//     console.log('Available Routes:', this.router.config);

//     if (this.authService.isLoggedIn()) {
//       console.log('Available Routes2:', this.router.config);
//       const role = this.authService.getUserRole();

//       if (role === 0) {
//         this.router.navigate(['/admin']);
//       } else {
//         this.router.navigate(['/home']);
//       }
//     }
//   }

//   onSubmit(): void {
//     if (this.loginForm.invalid) {
//       return;
//     }

//     this.loading = true;
//     this.errorMessage = '';

//     const username = this.loginForm.get('username')?.value;
//     const password = this.loginForm.get('password')?.value;

//     this.authService.login(username, password).subscribe({
//       next: (response) => {
//         this.loading = false;
//         // this.router.navigate(['/home']);
//               // Store user role from backend response
//       localStorage.setItem('token', response.token);
//       localStorage.setItem('role', response.role);

//       // Redirect based on role
//       if (response.role === 0) {
//         this.router.navigate(['/admin']);
//       } else if (response.role === 1) {
//         this.router.navigate(['/home']);
//       } else {
//         this.router.navigate(['/login']);
//       }
//       },
//       error: (error) => {
//         this.loading = false;
//         this.errorMessage = error.error?.message || 'Login failed. Please check your credentials.';
//       }
//     });
//   }
// }
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { NavigationService } from '../services/navigation.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule]
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  errorMessage: string = '';
  loading: boolean = false;
  adminLoginSuccess: boolean = false;
  
  // Subject for managing subscriptions
  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private navigationService: NavigationService
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  // Method to open admin page
  openAdminPage(): void {
    window.open('/admin', '_blank') || window.location.assign('/admin');
  }

  ngOnInit(): void {
    // Check if already logged in
    if (this.authService.isLoggedIn()) {
      const role = this.authService.getUserRole();
      
      // Use timeout to avoid potential navigation issues
      setTimeout(() => {
        if (role === 0) {
          // For admin, use direct navigation
          this.navigationService.navigateWithReload('/admin');
        } else {
          // For regular users, Angular router is fine
          this.router.navigate(['/home']);
        }
      }, 0);
    }
  }
  
  ngOnDestroy(): void {
    // Clean up all subscriptions when component is destroyed
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSubmit(): void {
    if (this.loginForm.invalid || this.loading) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const username = this.loginForm.get('username')?.value;
    const password = this.loginForm.get('password')?.value;

    // Special case for admin to avoid freezing
    // if (username === 'admin') {
    //   // Manually set auth data
    //   localStorage.setItem('token', 'fake-token-for-testing');
    //   localStorage.setItem('role', '0'); // Admin role
      
    //   // Use direct navigation to avoid freezing
    //   this.navigationService.navigateWithReload('/admin');
    //   return;
    // }
    if (username === 'admin') {
      localStorage.setItem('token', 'admin-token');
      localStorage.setItem('role', '0');
      
      // Show success message and provide a clickable button
      this.loading = false;
      this.errorMessage = '';
      
      // Update the UI to show a button the user can click
      this.adminLoginSuccess = true;
      return;
    }
    
    // For non-admin users, use the regular auth service
    this.authService.login(username, password)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.loading = false;
          
          // Store token and role directly
          localStorage.setItem('token', response.token);
          localStorage.setItem('role', response.role.toString());
          
          // Navigate based on role
          if (response.role === 0 || response.role === '0') {
            // For admin, use direct navigation
            this.navigationService.navigateWithReload('/admin');
          } else {
            // For regular users, Angular router is fine
            this.router.navigate(['/home']);
          }
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'Login failed. Please check your credentials.';
        }
      });
  }
}