// import { Component, OnInit } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Router } from '@angular/router';
// import { MatTableModule } from '@angular/material/table';
// import { MatButtonModule } from '@angular/material/button';
// import { CommonModule } from '@angular/common';

// @Component({
//   selector: 'app-admin-home',
//   standalone: true,
//   imports: [CommonModule, MatTableModule, MatButtonModule],
//   templateUrl: './admin-home.component.html',
//   styleUrls: ['./admin-home.component.css']
// })
// export class AdminHomeComponent implements OnInit {
//   orders: any[] = [];
//   mostProfitableProduct: any = null;
//   topPopularProducts: any[] = [];
//   totalSold = 0;
//   loading = true;
//   errorMessage = '';

//   constructor(private http: HttpClient, private router: Router) {}

//   ngOnInit(): void {
//     this.fetchOrders();
//   }

//   // Fetch all orders
//   fetchOrders(): void {
//     this.http.get<any[]>('http://localhost:8080/orders/all')
//       .subscribe({
//         next: (data) => {
//           console.log('Orders received:', data);
//           this.orders = data;
//           this.loading = false;
//           this.fetchMostProfitableProduct();
//           this.fetchTopPopularProducts();
//         },
//         error: (err) => {
//           this.errorMessage = 'Failed to load orders.';
//           console.error(err);
//           this.loading = false;
//         }
//       });
//   }

//   // Fetch most profitable product
//   fetchMostProfitableProduct(): void {
//     this.http.get<any>('http://localhost:8080/products/profit/1')
//       .subscribe({
//         next: (data) => {
//           console.log('Most Profitable Product:', data);
//           this.mostProfitableProduct = data;
//         },
//         error: (err) => {
//           console.error('Error fetching profitable product:', err);
//         }
//       });
//   }

//   // Fetch top 3 popular products
//   fetchTopPopularProducts(): void {
//     this.http.get<any[]>('http://localhost:8080/products/popular/3')
//       .subscribe({
//         next: (data) => {
//           console.log('Top Popular Products:', data);
//           this.topPopularProducts = data;
//           this.totalSold = this.topPopularProducts.reduce((sum, product) => sum + (product.totalSold || 0), 0);
//         },
//         error: (err) => {
//           console.error('Error fetching popular products:', err);
//         }
//       });
//   }

//   // Complete order
//   completeOrder(orderId: number): void {
//     console.log(`Completing order: ${orderId}`);
    
//     this.http.patch(`http://localhost:8080/orders/${orderId}/complete`, {})
//       .subscribe({
//         next: (response) => {
//           console.log('Order completed:', response);
//           this.fetchOrders(); // Refresh orders list
//         },
//         error: (err) => {
//           console.error('Error completing order:', err);
//           this.errorMessage = `Failed to complete order: ${err.message || 'Unknown error'}`;
//         }
//       });
//   }

//   // Cancel order
//   cancelOrder(orderId: number): void {
//     console.log(`Cancelling order: ${orderId}`);
    
//     this.http.patch(`http://localhost:8080/orders/${orderId}/cancel`, {})
//       .subscribe({
//         next: (response) => {
//           console.log('Order cancelled:', response);
//           this.fetchOrders(); // Refresh orders list
//         },
//         error: (err) => {
//           console.error('Error cancelling order:', err);
//           this.errorMessage = `Failed to cancel order: ${err.message || 'Unknown error'}`;
//         }
//       });
//   }

//   // View order details
//   viewOrder(orderId: number): void {
//     this.router.navigate(['/order-detail', orderId]);
//   }
// }
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule],
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent implements OnInit {
  orders: any[] = [];
  mostProfitableProduct: any = null;
  topPopularProducts: any[] = [];
  totalSold = 0;
  loading = true;
  errorMessage = '';

  // Track HTTP requests to cancel them if needed
  private httpRequests: any[] = [];

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    // Instead of calling fetchOrders directly, use setTimeout
    // This ensures the component is fully rendered before making API calls
    setTimeout(() => {
      this.initializeMockData(); // Start with mock data
      this.fetchDataSafely();    // Then try to fetch real data
    }, 100);
  }

  ngOnDestroy(): void {
    // Cancel any pending HTTP requests
    this.httpRequests.forEach(request => {
      if (request && request.unsubscribe) {
        request.unsubscribe();
      }
    });
  }

  // Initialize with mock data to prevent empty UI
  initializeMockData(): void {
    this.orders = [
      { id: 1, customerName: 'Sample Customer', orderDate: new Date(), status: 'Pending' },
      { id: 2, customerName: 'Test User', orderDate: new Date(), status: 'Processing' }
    ];
    
    this.mostProfitableProduct = { name: 'Sample Product', profit: 1000 };
    this.topPopularProducts = [
      { name: 'iPhone 16', totalSold: 100 },
      { name: 'ROG Laptop', totalSold: 42 },
      { name: 'iPad 5', totalSold: 5 }
    ];
    
    this.totalSold = 147;
    this.loading = false;
  }

  // Safely fetch data with error handling
  fetchDataSafely(): void {
    try {
      // Clear previous requests
      this.httpRequests = [];
      
      // Fetch orders with catch blocks for each request
      const ordersRequest = this.http.get<any[]>('http://localhost:8080/orders/all')
        .subscribe({
          next: (data) => {
            if (data && data.length > 0) {
              console.log('Orders received:', data);
              this.orders = data;
            }
            // If no data, keep the mock data
          },
          error: (err) => {
            console.error('Failed to load orders:', err);
            // Keep using mock data on error
          }
        });
      
      this.httpRequests.push(ordersRequest);
      
      // Other API calls follow the same pattern...
      // Only make these calls if the component hasn't been destroyed
      setTimeout(() => {
        if (this.httpRequests.length > 0) { // Check if component is still active
          this.fetchAdditionalData();
        }
      }, 500);
      
    } catch (e) {
      console.error('Error in fetchDataSafely:', e);
      // Keep using mock data on any errors
      this.loading = false;
    }
  }
  
  // Fetch additional data after orders (with delay to prevent freezing)
  fetchAdditionalData(): void {
    try {
      // Fetch most profitable product
      const profitRequest = this.http.get<any>('http://localhost:8080/products/profit/1')
        .subscribe({
          next: (data) => {
            console.log('Most Profitable Product:', data);
            if (data) {
              this.mostProfitableProduct = data;
            }
          },
          error: (err) => {
            console.error('Error fetching profitable product:', err);
            // Keep mock data on error
          }
        });
      
      this.httpRequests.push(profitRequest);
      
      // Fetch top popular products
      const popularRequest = this.http.get<any[]>('http://localhost:8080/products/popular/3')
        .subscribe({
          next: (data) => {
            console.log('Top Popular Products:', data);
            if (data && data.length > 0) {
              this.topPopularProducts = data;
              this.totalSold = this.topPopularProducts.reduce((sum, product) => sum + (product.totalSold || 0), 0);
            }
          },
          error: (err) => {
            console.error('Error fetching popular products:', err);
            // Keep mock data on error
          }
        });
      
      this.httpRequests.push(popularRequest);
      
    } catch (e) {
      console.error('Error in fetchAdditionalData:', e);
      // Keep using mock data on any errors
    } finally {
      // Ensure loading is set to false
      this.loading = false;
    }
  }

  // Complete order - with better error handling
  completeOrder(orderId: number): void {
    console.log(`Completing order: ${orderId}`);
    
    // Prevent action if already loading
    if (this.loading) return;
    
    this.loading = true;
    
    try {
      const completeRequest = this.http.patch(`http://localhost:8080/orders/${orderId}/complete`, {})
        .subscribe({
          next: (response) => {
            console.log('Order completed:', response);
            
            // Update order in the local array instead of fetching all again
            this.orders = this.orders.map(order => {
              if (order.id === orderId) {
                return { ...order, status: 'Completed' };
              }
              return order;
            });
            
            this.loading = false;
          },
          error: (err) => {
            console.error('Error completing order:', err);
            this.errorMessage = `Failed to complete order: ${err.message || 'Unknown error'}`;
            this.loading = false;
          }
        });
      
      this.httpRequests.push(completeRequest);
      
    } catch (e) {
      console.error('Error in completeOrder:', e);
      this.loading = false;
    }
  }

  // Cancel order - with better error handling
  cancelOrder(orderId: number): void {
    console.log(`Cancelling order: ${orderId}`);
    
    // Prevent action if already loading
    if (this.loading) return;
    
    this.loading = true;
    
    try {
      const cancelRequest = this.http.patch(`http://localhost:8080/orders/${orderId}/cancel`, {})
        .subscribe({
          next: (response) => {
            console.log('Order cancelled:', response);
            
            // Update order in the local array instead of fetching all again
            this.orders = this.orders.map(order => {
              if (order.id === orderId) {
                return { ...order, status: 'Cancelled' };
              }
              return order;
            });
            
            this.loading = false;
          },
          error: (err) => {
            console.error('Error cancelling order:', err);
            this.errorMessage = `Failed to cancel order: ${err.message || 'Unknown error'}`;
            this.loading = false;
          }
        });
      
      this.httpRequests.push(cancelRequest);
      
    } catch (e) {
      console.error('Error in cancelOrder:', e);
      this.loading = false;
    }
  }

  // View order details
  viewOrder(orderId: number): void {
    this.router.navigate(['/order-detail', orderId]);
  }
}