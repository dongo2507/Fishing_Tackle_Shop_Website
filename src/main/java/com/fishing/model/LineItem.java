package com.fishing.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Một dòng sản phẩm trong đơn hàng.
 * unitPrice và productName được CHỐT tại thời điểm đặt: sau này Admin đổi giá
 * hay đổi tên sản phẩm thì đơn cũ vẫn đúng số tiền khách đã trả.
 */
@Entity
@Table(name = "line_item")
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Giữ liên kết để hoàn kho khi hủy đơn và để mở lại trang sản phẩm
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    public LineItem() {}

    /** Tạo LineItem từ một dòng giỏ hàng, chốt giá hiện tại. */
    public static LineItem fromCartItem(CartItem ci) {
        Product p = ci.getProduct();
        LineItem li = new LineItem();
        li.setProduct(p);
        li.setProductName(p.getName());
        li.setThumbnailUrl(p.getThumbnailUrl());
        li.setUnitPrice(p.getPrice());
        li.setQuantity(ci.getQuantity());
        return li;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
