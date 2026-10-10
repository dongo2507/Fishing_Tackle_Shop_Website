package com.fishing.dao;

import com.fishing.model.Product;
import com.fishing.model.ProductImage;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ProductImageDAO {

    public List<ProductImage> findByProduct(Long productId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT i FROM ProductImage i WHERE i.product.id = :pid ORDER BY i.id",
                    ProductImage.class)
                    .setParameter("pid", productId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countByProduct(Long productId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(i) FROM ProductImage i WHERE i.product.id = :pid", Long.class)
                    .setParameter("pid", productId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /** Thêm nhiều ảnh vào album của sản phẩm trong một transaction. */
    public void add(Long productId, List<String> urls) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Product ref = em.getReference(Product.class, productId);
            for (String url : urls) {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setProduct(ref);
                em.persist(img);
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

    /**
     * Xóa một ảnh của sản phẩm.
     * @return đường dẫn ảnh đã xóa (để xóa file thật), hoặc null nếu ảnh không tồn tại / không thuộc sản phẩm
     */
    public String delete(Long productId, Long imageId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            ProductImage img = em.find(ProductImage.class, imageId);
            if (img == null || !img.getProduct().getId().equals(productId)) {
                em.getTransaction().rollback();
                return null;
            }
            String url = img.getImageUrl();
            em.remove(img);
            em.getTransaction().commit();
            return url;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Đặt ảnh đại diện mới (newUrl = null nghĩa là xóa ảnh đại diện).
     * @return đường dẫn ảnh đại diện cũ để xóa file
     */
    public String replaceThumbnail(Long productId, String newUrl) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Product p = em.find(Product.class, productId);
            if (p == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Không tìm thấy sản phẩm.");
            }
            String old = p.getThumbnailUrl();
            p.setThumbnailUrl(newUrl);
            em.getTransaction().commit();
            return old;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
