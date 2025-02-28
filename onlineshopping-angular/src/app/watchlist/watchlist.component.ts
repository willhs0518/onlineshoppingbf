// watchlist.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-watchlist',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink, 
    MatTableModule, 
    MatButtonModule, 
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})
export class WatchlistComponent implements OnInit {
  watchlistItems: any[] = [];
  displayedColumns: string[] = ['name', 'description', 'price', 'actions'];
  loading = true;
  error = '';
  
  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.fetchWatchlist();
  }

  fetchWatchlist(): void {
    this.http.get<any[]>('http://localhost:8080/watchlist/products/all')
      .subscribe({
        next: (data) => {
          this.watchlistItems = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load watchlist.';
          console.error(err);
          this.loading = false;
        }
      });
  }

// watchlist.component.ts - fix removeFromWatchlist method
// In watchlist.component.ts
removeFromWatchlist(productId: number, itemIndex: number): void {
  console.log('Removing from watchlist, product ID:', productId);
  
  // First remove it from the UI to give immediate feedback
  const removedItem = this.watchlistItems[itemIndex];
  this.watchlistItems.splice(itemIndex, 1);
  
  this.http.delete(`http://localhost:8080/watchlist/product/${productId}`, 
    { responseType: 'text' })
    .subscribe({
      next: () => {
        this.snackBar.open('Item removed from watchlist', 'Close', {
          duration: 3000
        });
      },
      error: (err) => {
        if (err.status === 200) {
          // If we got a 200 status, it probably worked
          this.snackBar.open('Item removed from watchlist', 'Close', {
            duration: 3000
          });
        } else {
          // If there was a real error, add the item back
          console.error('Error removing from watchlist:', err);
          this.watchlistItems.splice(itemIndex, 0, removedItem);
          this.snackBar.open('Failed to remove from watchlist', 'Close', {
            duration: 3000
          });
        }
      }
    });
}

  addToCart(product: any): void {
    // Get cart from localStorage
    let cart: any[] = [];
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      cart = JSON.parse(savedCart);
    }
    
    // Check if product is already in cart
    const existingItem = cart.find(item => item.id === product.productId);
    
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      cart.push({
        id: product.productId,
        name: product.name,
        price: product.retailPrice,
        quantity: 1
      });
    }
    
    // Save to localStorage
    localStorage.setItem('cart', JSON.stringify(cart));
    
    this.snackBar.open(`${product.name} added to cart`, 'Close', {
      duration: 3000
    });
  }
}