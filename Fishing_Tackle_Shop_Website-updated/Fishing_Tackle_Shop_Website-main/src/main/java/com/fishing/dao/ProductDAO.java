package com.fishing.dao;

import com.fishing.enums.ProductStatus;
import com.fishing.model.Brand;
import com.fishing.model.Category;
import com.fishing.model.Product;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProductDAO {

    /**
     * Tìm kiếm + lọc. Tham số nào null/rỗng thì bỏ qua điều kiện đó.
     * JOIN FETCH category và brand để JSP đọc được sau khi EntityManager đã đóng.
     */
    public List<Product> search(String keyword, Long brandId, ProductStatus status) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            boolean hasKeyword = keyword != null && !keyword.isBlank();

            StringBuilder jpql = new StringBuilder(
                    "SELECT p FROM Product p "
                            + "LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand WHERE 1 = 1");
            if (hasKeyword) {
                jpql.append(" AND LOWER(p.name) LIKE LOWER(:kw) ESCAPE '!'");
            }
            if (brandId != null) {
                jpql.append(" AND p.brand.id = :brandId");
            }
            if (status != null) {
                jpql.append(" AND p.status = :status");
            }
            jpql.append(" ORDER BY p.id DESC");

            TypedQuery<Product> q = em.createQuery(jpql.toString(), Product.class);
            if (hasKeyword) {
                q.setParameter("kw", "%" + escapeLike(keyword.trim()) + "%");
            }
            if (brandId != null) {
                q.setParameter("brandId", brandId);
            }
            if (status != null) {
                q.setParameter("status", status);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Product findById(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Product p "
                                    + "LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand "
                                    + "WHERE p.id = :id", Product.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    /** id == null: thêm mới (persist); ngược lại: cập nhật (merge). */
    public Product save(Product product) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();

            // Chỉ cần tham chiếu tới category/brand (không cần nạp lại dữ liệu)
            if (product.getCategory() != null) {
                product.setCategory(em.getReference(Category.class, product.getCategory().getId()));
            }
            if (product.getBrand() != null) {
                product.setBrand(em.getReference(Brand.class, product.getBrand().getId()));
            }

            Product saved;
            if (product.getId() == null) {
                em.persist(product);
                saved = product;
            } else {
                saved = em.merge(product);
            }
            em.getTransaction().commit();
            return saved;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Xóa sản phẩm (cascade xóa luôn ảnh, đánh giá). Ném lỗi nếu bị bảng khác tham chiếu. */
    public void delete(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Product p = em.find(Product.class, id);
            if (p != null) {
                em.remove(p);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Đổi trạng thái: entity đang được quản lý nên chỉ cần set, Hibernate tự UPDATE khi commit. */
    public void updateStatus(Long id, ProductStatus status) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Product p = em.find(Product.class, id);
            if (p != null) {
                p.setStatus(status);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Escape ký tự đặc biệt của LIKE để người dùng gõ % hoặc _ vẫn tìm đúng nghĩa đen. */
    private String escapeLike(String s) {
        return s.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
