import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-home',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule],
  templateUrl: './userhome.component.html',
  styleUrls: ['./userhome.component.css']
})
export class UserHomeComponent implements OnInit {
  orders: any[] = [];
  topFrequentItems: any[] = [];
  topRecentItems: any[] = [];
  loading = true;
  errorMessage = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.fetchOrders();
    // this.fetchTopFrequentItems();
    // this.fetchTopRecentItems();
  }

  // Fetch user orders
  fetchOrders(): void {
    this.http.get<any[]>('http://localhost:8080/orders/all')
      .subscribe({
        next: (data) => {
          console.log('Orders data received:', data);
          this.orders = data;
          this.loading = false;

          this.fetchTopFrequentItems();
          this.fetchTopRecentItems();
        },
        error: (err) => {
          this.errorMessage = 'Failed to load orders.';
          console.error(err);
          this.loading = false;
        }
      });
  }

  // Fetch top 3 most frequently purchased items
  fetchTopFrequentItems(): void {
    this.http.get<any[]>('http://localhost:8080/products/frequent/3')
      .subscribe({
        next: (data) => this.topFrequentItems = data,
        error: (err) => console.error('Error fetching frequent items:', err)
      });
  }

  // Fetch top 3 most recently purchased items
  fetchTopRecentItems(): void {
    this.http.get<any[]>('http://localhost:8080/products/recent/3')
      .subscribe({
        next: (data) => this.topRecentItems = data,
        error: (err) => console.error('Error fetching recent items:', err)
      });
  }

  // Cancel order
  cancelOrder(orderId: number): void {
    console.log(`Attempting to cancel order: ${orderId}`);
    
    this.http.patch(`http://localhost:8080/orders/${orderId}/cancel`, {})
      .subscribe({
        next: (response) => {
          console.log('Cancel successful:', response);
          this.fetchOrders(); // Refresh the orders list
        },
        error: (err) => {
          console.error('Error canceling order:', err);
          // Show the user an error message
          this.errorMessage = `Failed to cancel order: ${err.message || 'Unknown error'}`;
        }
      });
  }

  // View order details
  viewOrder(orderId: number): void {
    this.router.navigate(['/order-detail', orderId]);
  }
}
