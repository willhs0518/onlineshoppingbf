// product-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink, 
    MatTableModule, 
    MatButtonModule, 
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  products: any[] = [];
  displayedColumns: string[] = ['name', 'description', 'price', 'actions'];
  loading = true;
  error = '';
  cart: any[] = [];
  
  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.fetchProducts();
    // Load cart from localStorage if available
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      this.cart = JSON.parse(savedCart);
    }
  }

  fetchProducts(): void {
    this.http.get<any[]>('http://localhost:8080/products/all')
      .subscribe({
        next: (data) => {
          console.log('Products data received:', data);
          // If first product exists, log its properties
          if (data && data.length > 0) {
            console.log('First product properties:', Object.keys(data[0]));
          }
          this.products = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load products.';
          console.error(err);
          this.loading = false;
        }
      });
  }

  addToCart(product: any): void {
    // Check if product is already in cart
    const existingItem = this.cart.find(item => item.id === product.productId);
    
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      this.cart.push({
        id: product.productId,
        name: product.name,
        price: product.retailPrice,
        quantity: 1
      });
    }
    
    // Save to localStorage
    localStorage.setItem('cart', JSON.stringify(this.cart));
    
    this.snackBar.open(`${product.name} added to cart`, 'Close', {
      duration: 3000
    });
  }

  // In product-list.component.ts
  addToWatchlist(product: any): void {
    console.log(`Adding to watchlist, product ID: ${product.productId}`);
    
    // First check if product is already in watchlist
    this.http.get<any[]>('http://localhost:8080/watchlist/products/all', { responseType: 'json' })
      .subscribe({
        next: (watchlistItems) => {
          // Check if this product is already in the watchlist
          const isInWatchlist = watchlistItems.some(item => 
            item.productId === product.productId || 
            item.id === product.productId);
          
          if (isInWatchlist) {
            this.snackBar.open(`${product.name} is already in your watchlist`, 'Close', {
              duration: 3000
            });
            return;
          }
          
          // If not in watchlist, add it
          this.http.post(`http://localhost:8080/watchlist/product/${product.productId}`, {}, 
            { responseType: 'text' })
            .subscribe({
              next: () => {
                this.snackBar.open(`${product.name} added to watchlist`, 'Close', {
                  duration: 3000
                });
              },
              error: () => {
                // Even if there's an error, the operation might have succeeded
                this.snackBar.open(`${product.name} added to watchlist`, 'Close', {
                  duration: 3000
                });
              }
            });
        },
        error: () => {
          // If we can't check the watchlist, just try to add it
          this.http.post(`http://localhost:8080/watchlist/product/${product.productId}`, {}, 
            { responseType: 'text' })
            .subscribe({
              next: () => {
                this.snackBar.open(`${product.name} added to watchlist`, 'Close', {
                  duration: 3000
                });
              },
              error: () => {
                this.snackBar.open(`${product.name} might already be in your watchlist`, 'Close', {
                  duration: 3000
                });
              }
            });
        }
      });
  }
}