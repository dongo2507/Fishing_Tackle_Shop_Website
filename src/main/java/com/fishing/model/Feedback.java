package com.fishing.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1-5, kiểm tra trong Servlet trước khi lưu
    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    // Tạm dùng số, sau này đổi thành @ManyToOne Customer (phần của Dũng)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<SubFeedback> subFeedbacks = new ArrayList<>();

    public Feedback() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public List<SubFeedback> getSubFeedbacks() { return subFeedbacks; }
    public void setSubFeedbacks(List<SubFeedback> subFeedbacks) { this.subFeedbacks = subFeedbacks; }

    public void addSubFeedback(SubFeedback subFeedback) {
        subFeedbacks.add(subFeedback);
        subFeedback.setFeedback(this);
    }
}
