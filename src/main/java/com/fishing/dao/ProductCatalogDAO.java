package com.fishing.dao;

import com.fishing.enums.ProductStatus;
import com.fishing.model.Product;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Truy vấn danh sách sản phẩm cho KHÁCH HÀNG.
 * Chỉ lấy sản phẩm đang bán hoặc hết hàng (không lấy HIDDEN, DISCONTINUED)
 * và thuộc danh mục đang hiện.
 */
public class ProductCatalogDAO {

    private static final List<ProductStatus> LISTED =
            List.of(ProductStatus.ACTIVE, ProductStatus.OUT_OF_STOCK);

    public List<Product> search(String keyword, Long brandId, Long categoryId,
                                String sort, int page, int size) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            String jpql = "SELECT p FROM Product p JOIN FETCH p.category c LEFT JOIN FETCH p.brand b"
                    + where(keyword, brandId, categoryId)
                    + " ORDER BY " + orderBy(sort);
            TypedQuery<Product> q = em.createQuery(jpql, Product.class);
            bind(q, keyword, brandId, categoryId);
            q.setFirstResult((page - 1) * size);
            q.setMaxResults(size);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public long count(String keyword, Long brandId, Long categoryId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            String jpql = "SELECT COUNT(p) FROM Product p JOIN p.category c"
                    + where(keyword, brandId, categoryId);
            TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            bind(q, keyword, brandId, categoryId);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }

    /** Điểm trung bình và số đánh giá của nhiều sản phẩm bằng MỘT câu truy vấn (tránh gọi lặp từng sản phẩm). */
    public Map<Long, RatingSummary> getRatings(List<Long> productIds) {
        Map<Long, RatingSummary> map = new HashMap<>();
        if (productIds.isEmpty()) {
            return map;
        }
        EntityManager em = JPAUtil.createEntityManager();
        try {
            List<Object[]> rows = em.createQuery(
                            "SELECT f.product.id, AVG(f.rating), COUNT(f) FROM Feedback f "
                                    + "WHERE f.product.id IN :ids GROUP BY f.product.id", Object[].class)
                    .setParameter("ids", productIds)
                    .getResultList();
            for (Object[] r : rows) {
                map.put(((Number) r[0]).longValue(),
                        new RatingSummary(((Number) r[1]).doubleValue(), ((Number) r[2]).longValue()));
            }
            return map;
        } finally {
            em.close();
        }
    }

    // ---------- Hàm hỗ trợ ----------

    /** Phần WHERE dùng chung cho câu lấy danh sách và câu đếm. Chỉ nối chuỗi CỐ ĐỊNH, dữ liệu đi qua tham số. */
    private String where(String keyword, Long brandId, Long categoryId) {
        StringBuilder sb = new StringBuilder(" WHERE p.status IN :statuses AND c.visible = true");
        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND LOWER(p.name) LIKE LOWER(:kw) ESCAPE '!'");
        }
        if (brandId != null) {
            sb.append(" AND p.brand.id = :brandId");
        }
        if (categoryId != null) {
            sb.append(" AND c.id = :categoryId");
        }
        return sb.toString();
    }

    private void bind(TypedQuery<?> q, String keyword, Long brandId, Long categoryId) {
        q.setParameter("statuses", LISTED);
        if (keyword != null && !keyword.isBlank()) {
            q.setParameter("kw", "%" + escapeLike(keyword.trim()) + "%");
        }
        if (brandId != null) {
            q.setParameter("brandId", brandId);
        }
        if (categoryId != null) {
            q.setParameter("categoryId", categoryId);
        }
    }

    /** Chỉ cho phép các giá trị có trong danh sách, không bao giờ nối chuỗi từ người dùng vào ORDER BY. */
    private String orderBy(String sort) {
        if ("price_asc".equals(sort)) {
            return "p.price ASC, p.id DESC";
        }
        if ("price_desc".equals(sort)) {
            return "p.price DESC, p.id DESC";
        }
        return "p.createdAt DESC, p.id DESC";   // newest (mặc định)
    }

    private String escapeLike(String s) {
        return s.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
