package com.example.onlineshopping.domain.hibernate;

import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "product")
public class ProductHibernate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @NotBlank(message = "Product name cannot be empty")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Min(value = 0, message = "Quantity must be at least 0")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @DecimalMin(value = "0.01", message = "Retail price must be greater than 0")
    @Column(name = "retail_price", nullable = false)
    private double retailPrice;

    @DecimalMin(value = "0.01", message = "Wholesale price must be greater than 0")
    @Column(name = "wholesale_price", nullable = false)
    private double wholesalePrice;

    public Long getProductId() { return productId; }
    public String getName() {return name;}
    public String getDescription() {return description;}
    public int getQuantity() {return quantity;}
    public double getRetailPrice() {return retailPrice;}
    public double getWholesalePrice() {return wholesalePrice;}

    public void setQuantity(int quantity) {this.quantity = quantity;}
    public void setName(String name) {this.name = name;}
    public void setDescription(String description) {this.description = description;}
    public void setRetailPrice(double retailPrice) { this.retailPrice = retailPrice;}
    public void setWholesalePrice(double wholesalePrice) {this.wholesalePrice = wholesalePrice;}

    public ProductHibernate(Long productId, String name, String description, Double retailPrice) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.retailPrice = retailPrice;
    }

    public ProductHibernate() {}

}
