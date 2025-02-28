// order-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router'; // Add this import

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, MatButtonModule, RouterLink, ],
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.css']
})
export class OrderDetailComponent implements OnInit {
  orderId: number | null = null;
  orderDetails: any = null;
  loading = true;
  error = '';
  isAdmin = false; // Track if the user is an admin


  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.orderId = Number(params.get('id'));
      if (this.orderId) {
        this.fetchOrderDetails();
      }
    });
    this.isAdmin = localStorage.getItem('role') === '0'; // Role "0" means admin

  }

  fetchOrderDetails(): void {
    this.http.get<any>(`http://localhost:8080/orders/${this.orderId}`)
      .subscribe({
        next: (data) => {
          this.orderDetails = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load order details.';
          console.error(err);
          this.loading = false;
        }
      });
  }

  calculateTotal(): number {
    if (!this.orderDetails || !this.orderDetails.items) return 0;
    return this.orderDetails.items.reduce((total: number, item: any) => {
      return total + (item.price * item.quantity);
    }, 0);
  }

  goBack(): void {
    if (this.isAdmin) {
      this.router.navigate(['/admin']);
    } else {
      this.router.navigate(['/home']);
    }
  }
}
