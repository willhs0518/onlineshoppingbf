
import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { RouterLink } from '@angular/router'; 
import { NavigationService } from '../services/navigation.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnDestroy {
  signupForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;
  
  // Subject for managing subscriptions
  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private navigationService: NavigationService
  ) {
    this.signupForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }
  
  ngOnDestroy(): void {
    // Clean up all subscriptions when component is destroyed
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSubmit(): void {
    if (this.signupForm.invalid) return;
  
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
  
    this.authService.register(this.signupForm.value)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('Signup successful:', response);
  
          if (response?.message) {
            this.successMessage = response.message;
            this.errorMessage = '';
  
            // Use direct navigation with reload to prevent freezing
            setTimeout(() => {
              this.navigationService.navigateWithReload('/login');
            }, 2000);
          } else {
            this.errorMessage = 'Unexpected response format. Please try again.';
          }
          this.loading = false;
        },
        error: (error) => {
          this.loading = false;
          console.error('Signup error:', error);
          this.errorMessage = error.error?.message || 'Signup failed. Please try again.';
        }
      });
  }
}