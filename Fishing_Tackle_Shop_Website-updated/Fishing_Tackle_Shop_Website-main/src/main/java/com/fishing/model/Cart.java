package com.fishing.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Giỏ hàng lưu trong database: mỗi khách hàng có đúng 1 giỏ.
 * Lưu DB (không chỉ session) để đăng xuất / đổi máy vẫn còn giỏ.
 */
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tạm dùng số, sau này đổi thành @OneToOne Customer (phần của Dũng)
    @Column(name = "customer_id", nullable = false, unique = true)
    private Long customerId;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CartItem> items = new ArrayList<>();

    public Cart() {}

    public Cart(Long customerId) {
        this.customerId = customerId;
    }

    /** Tổng tiền tạm tính = tổng (giá hiện tại x số lượng) của các món còn mua được. */
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item.isAvailable()) {
                total = total.add(item.getSubtotal());
            }
        }
        return total;
    }

    /** Tổng số lượng sản phẩm (để hiện số trên biểu tượng giỏ hàng). */
    public int getTotalQuantity() {
        int sum = 0;
        for (CartItem item : items) {
            sum += item.getQuantity();
        }
        return sum;
    }

    /** Có món nào đang hết hàng / ngừng bán / vượt tồn kho không. */
    public boolean hasProblem() {
        for (CartItem item : items) {
            if (!item.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public CartItem findItem(Long productId) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    // Cho JSP gọi ${cart.total}
    public BigDecimal getTotal() { return calculateTotal(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}
