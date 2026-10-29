package com.fishing.model;

import com.fishing.enums.ProductStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

/** Một dòng trong giỏ hàng. Mỗi sản phẩm chỉ xuất hiện 1 lần trong 1 giỏ. */
@Entity
@Table(name = "cart_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Admin xóa sản phẩm thì dòng giỏ hàng tự mất theo (ON DELETE CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    public CartItem() {}

    /**
     * Giỏ hàng KHÔNG lưu giá: luôn lấy giá hiện tại của sản phẩm.
     * Giá chỉ được "chốt" khi đặt hàng (LineItem.unitPrice).
     */
    public BigDecimal getUnitPrice() {
        return product.getPrice();
    }

    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /** Còn mua được với số lượng này không. */
    public boolean isAvailable() {
        return product.getStatus() == ProductStatus.ACTIVE
                && product.getStockQuantity() >= quantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
