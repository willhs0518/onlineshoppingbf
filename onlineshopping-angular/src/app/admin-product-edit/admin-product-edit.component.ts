import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-admin-product-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule],
  templateUrl: './admin-product-edit.component.html',
  styleUrls: ['./admin-product-edit.component.css']
})
export class AdminProductEditComponent implements OnInit {
  productForm: FormGroup;
  productId: number | null = null;
  loading = true;
  errorMessage = '';
  isAddingProduct = false;


  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    public router: Router,
    private fb: FormBuilder
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      retailPrice: ['', [Validators.required, Validators.min(0)]],
      wholesalePrice: ['', [Validators.required, Validators.min(0)]],
      quantity: ['', [Validators.required, Validators.min(0)]]
    });
  }

  // ngOnInit(): void {
  //   this.route.paramMap.subscribe(params => {
  //     this.productId = Number(params.get('id'));
  //     if (this.productId) {
  //       this.loadProduct();
  //     }
  //   });
  // }
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.productId = id ? Number(id) : null;
      this.isAddingProduct = this.productId === null;
      
      if (!this.isAddingProduct) {
        this.fetchProductDetails();
      } else {
        this.loading = false;
      }
    });
  }

  loadProduct(): void {
    this.http.get<any>(`http://localhost:8080/products/${this.productId}`)
      .subscribe({
        next: (product) => {
          this.productForm.patchValue(product);
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = 'Failed to load product details.';
          console.error(err);
          this.loading = false;
        }
      });
  }

  // saveProduct(): void {
  //   if (!this.productForm.valid || !this.productId) return;

  //   this.http.patch(`http://localhost:8080/products/${this.productId}`, this.productForm.value)
  //     .subscribe({
  //       next: () => {
  //         alert('Product updated successfully!');
  //         this.router.navigate(['/admin/products']);
  //       },
  //       error: (err) => {
  //         console.error('Error updating product:', err);
  //         this.errorMessage = `Update failed: ${err.error?.message || 'Unknown error'}`;
  //       }
  //     });
  // }
  saveProduct(): void {
    if (this.productForm.invalid) return;

    const productData = this.productForm.value;
    
    if (this.isAddingProduct) {
      this.http.post('http://localhost:8080/products', productData)
        .subscribe({
          next: () => this.router.navigate(['/admin/products']),
          error: (err) => this.errorMessage = 'Failed to add product.'
        });
    } else {
      this.http.patch(`http://localhost:8080/products/${this.productId}`, productData)
        .subscribe({
          next: () => this.router.navigate(['/admin/products']),
          error: (err) => this.errorMessage = 'Failed to update product.'
        });
    }
  }

  fetchProductDetails(): void {
    if (!this.productId) return;
  
    this.http.get<any>(`http://localhost:8080/products/${this.productId}`)
      .subscribe({
        next: (data) => {
          this.productForm.patchValue(data);
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = 'Failed to load product details.';
          this.loading = false;
        }
      });
  }
  cancel(): void {
    this.router.navigate(['/admin/products']);
  }

  
}
