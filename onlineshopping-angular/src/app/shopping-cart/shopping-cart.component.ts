// shopping-cart.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink, 
    MatTableModule, 
    MatButtonModule, 
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css']
})
export class ShoppingCartComponent implements OnInit {
  cartItems: any[] = [];
  displayedColumns: string[] = ['name', 'price', 'quantity', 'total', 'actions'];
  
  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCartItems();
  }

  loadCartItems(): void {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      this.cartItems = JSON.parse(savedCart);
    }
  }

  updateCart(): void {
    localStorage.setItem('cart', JSON.stringify(this.cartItems));
  }

  removeItem(item: any): void {
    this.cartItems = this.cartItems.filter(cartItem => cartItem.id !== item.id);
    this.updateCart();
    this.snackBar.open(`${item.name} removed from cart`, 'Close', {
      duration: 3000
    });
  }

  updateQuantity(item: any, change: number): void {
    const itemIndex = this.cartItems.findIndex(cartItem => cartItem.id === item.id);
    
    if (itemIndex !== -1) {
      this.cartItems[itemIndex].quantity += change;
      
      // Remove item if quantity becomes 0 or less
      if (this.cartItems[itemIndex].quantity <= 0) {
        this.cartItems.splice(itemIndex, 1);
      }
      
      this.updateCart();
    }
  }

  calculateTotal(): number {
    return this.cartItems.reduce((total, item) => {
      return total + (item.price * item.quantity);
    }, 0);
  }

  checkout(): void {
    // Check if cart is empty
    if (this.cartItems.length === 0) {
      this.snackBar.open('Your cart is empty', 'Close', {
        duration: 3000
      });
      return;
    }
  
    // Create order payload in the format the backend expects
    const orderItems = {
      order: this.cartItems.map(item => ({
        productId: item.id,
        quantity: item.quantity
      }))
    };
  
    console.log('Sending order payload:', orderItems);
  
    this.http.post('http://localhost:8080/orders', orderItems, { responseType: 'text' })
      .subscribe({
        next: (response) => {
          console.log('Order placed successfully:', response);
          // Clear cart
          this.cartItems = [];
          localStorage.removeItem('cart');
          
          this.snackBar.open('Order placed successfully!', 'Close', {
            duration: 3000
          });
        },
        error: (err) => {
          console.error('Error placing order:', err);
          
          // Even if we get an error response but with 200 status, consider it a success
          if (err.status === 200) {
            this.cartItems = [];
            localStorage.removeItem('cart');
            this.snackBar.open('Order placed successfully!', 'Close', {
              duration: 3000
            });
          } else {
            this.snackBar.open(`Failed to place order: ${err.error}`, 'Close', {
              duration: 3000
            });
          }
        }
      });
  }
}