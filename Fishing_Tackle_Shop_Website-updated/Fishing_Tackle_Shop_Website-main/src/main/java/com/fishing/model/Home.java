package com.fishing.model;

import com.fishing.dao.RatingSummary;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Model tổng hợp dữ liệu trang chủ (Home ViewModel)
 */
public class Home implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Category> categories;
    private List<Brand> brands;
    private List<Product> topRatedProducts;
    private List<Product> newestProducts;
    private Map<Long, RatingSummary> ratings; // Map product_id -> Object chứa trung bình sao & số lượt đánh giá

    public Home() {
    }

    public Home(List<Category> categories, List<Brand> brands,
                List<Product> topRatedProducts, List<Product> newestProducts,
                Map<Long, RatingSummary> ratings) {
        this.categories = categories;
        this.brands = brands;
        this.topRatedProducts = topRatedProducts;
        this.newestProducts = newestProducts;
        this.ratings = ratings;
    }

    // --- Getters and Setters ---

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Product> getTopRatedProducts() {
        return topRatedProducts;
    }

    public void setTopRatedProducts(List<Product> topRatedProducts) {
        this.topRatedProducts = topRatedProducts;
    }

    public List<Product> getNewestProducts() {
        return newestProducts;
    }

    public void setNewestProducts(List<Product> newestProducts) {
        this.newestProducts = newestProducts;
    }

    public Map<Long, RatingSummary> getRatings() {
        return ratings;
    }

    public void setRatings(Map<Long, RatingSummary> ratings) {
        this.ratings = ratings;
    }
}