// product-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  productId: number | null = null;
  product: any = null;
  loading = true;
  error = '';
  isAdmin = false;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  orderId: number | null = null;

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.productId = Number(params.get('id'));
      if (this.productId) {
        this.fetchProductDetails();
      }
    });
    
    this.route.queryParamMap.subscribe(params => {
      this.orderId = params.get('orderId') ? Number(params.get('orderId')) : null;
    });

        // Check user role from localStorage
    const role = localStorage.getItem('role');
    this.isAdmin = role === '0'; // Role 0 means Admin
  }
// In product-detail.component.ts
  fetchProductDetails(): void {
    if (!this.productId) return;

    console.log('Fetching product details for ID:', this.productId);

    this.http.get<any>(`http://localhost:8080/products/${this.productId}`)
      .subscribe({
        next: (data) => {
          console.log('Product detail data received:', data);
          this.product = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading product details:', err);
          this.error = 'Failed to load product details.';
          this.loading = false;
        }
      });
  }

  editProduct(): void {
    if (!this.productId) return;
    console.log('Navigating to edit product:', this.productId);
    window.location.href = `/admin/products/edit/${this.productId}`;
  }

  
}