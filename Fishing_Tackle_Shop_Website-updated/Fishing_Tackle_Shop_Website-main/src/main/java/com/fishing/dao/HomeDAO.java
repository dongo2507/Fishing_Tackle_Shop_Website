package com.fishing.dao;

import com.fishing.enums.ProductStatus;
import com.fishing.model.Product;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Truy vấn dành riêng cho trang chủ khách hàng.
 * Sản phẩm mới nhất đã có ở ProductCatalogDAO.search(..., "newest", ...),
 * nên ở đây chỉ cần nhóm "đánh giá cao nhất".
 */
public class HomeDAO {

    /** Giống ProductCatalogDAO: khách chỉ thấy sản phẩm đang bán hoặc hết hàng. */
    private static final List<ProductStatus> LISTED =
            List.of(ProductStatus.ACTIVE, ProductStatus.OUT_OF_STOCK);

    /**
     * Các sản phẩm có điểm đánh giá trung bình cao nhất.
     * Sắp xếp: điểm trung bình giảm dần, rồi số lượt đánh giá giảm dần, rồi sản phẩm mới hơn trước.
     * Sản phẩm chưa có đánh giá nào thì không xuất hiện trong danh sách.
     *
     * @param limit số sản phẩm tối đa cần lấy
     */
    public List<Product> topRated(int limit) {
        if (limit <= 0) {
            return new ArrayList<>();
        }
        EntityManager em = JPAUtil.createEntityManager();
        try {
            // Bước 1: lấy id theo thứ tự xếp hạng (GROUP BY không đi chung được với JOIN FETCH)
            List<Long> ids = em.createQuery(
                            "SELECT p.id FROM Feedback f JOIN f.product p JOIN p.category c "
                                    + "WHERE p.status IN :statuses AND c.visible = true "
                                    + "GROUP BY p.id "
                                    + "ORDER BY AVG(f.rating) DESC, COUNT(f) DESC, p.id DESC",
                            Long.class)
                    .setParameter("statuses", LISTED)
                    .setMaxResults(limit)
                    .getResultList();

            if (ids.isEmpty()) {
                return new ArrayList<>();
            }

            // Bước 2: nạp sản phẩm kèm danh mục và thương hiệu bằng một câu truy vấn
            List<Product> products = em.createQuery(
                            "SELECT p FROM Product p JOIN FETCH p.category LEFT JOIN FETCH p.brand "
                                    + "WHERE p.id IN :ids", Product.class)
                    .setParameter("ids", ids)
                    .getResultList();

            // Bước 3: IN không giữ thứ tự nên sắp xếp lại theo thứ tự xếp hạng ở bước 1
            Map<Long, Product> byId = new HashMap<>();
            for (Product p : products) {
                byId.put(p.getId(), p);
            }
            List<Product> ordered = new ArrayList<>();
            for (Long id : ids) {
                Product p = byId.get(id);
                if (p != null) {
                    ordered.add(p);
                }
            }
            return ordered;
        } finally {
            em.close();
        }
    }
}