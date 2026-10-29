package com.fishing.dao;

import com.fishing.enums.ProductStatus;
import com.fishing.model.Cart;
import com.fishing.model.CartItem;
import com.fishing.model.Product;
import com.fishing.util.BusinessException;
import com.fishing.util.JPAUtil;
import com.fishing.util.ProductRules;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/** Thao tác giỏ hàng. Mỗi khách hàng có 1 giỏ, tạo tự động ở lần thêm đầu tiên. */
public class CartDAO {

    /** Số lượng tối đa của 1 sản phẩm trong giỏ. */
    public static final int MAX_QTY_PER_ITEM = 99;

    /**
     * Lấy giỏ hàng kèm sản phẩm (JOIN FETCH để JSP đọc được sau khi đóng EntityManager).
     * Chưa có giỏ thì trả về giỏ rỗng (chưa lưu DB).
     */
    public Cart findByCustomer(Long customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            Cart cart = em.createQuery(
                            "SELECT DISTINCT c FROM Cart c "
                                    + "LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.product "
                                    + "WHERE c.customerId = :cid", Cart.class)
                    .setParameter("cid", customerId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            return cart != null ? cart : new Cart(customerId);
        } finally {
            em.close();
        }
    }

    /** Tổng số lượng sản phẩm trong giỏ (hiện trên thanh menu). */
    public int countQuantity(Long customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            // SUM của cột int trả về Long; giỏ trống thì SUM = null
            Long sum = em.createQuery(
                            "SELECT SUM(i.quantity) FROM CartItem i "
                                    + "WHERE i.cart.customerId = :cid", Long.class)
                    .setParameter("cid", customerId)
                    .getSingleResult();
            return sum == null ? 0 : sum.intValue();
        } finally {
            em.close();
        }
    }

    /** Thêm sản phẩm vào giỏ. Nếu đã có thì cộng dồn số lượng. */
    public void addItem(Long customerId, Long productId, int quantity) {
        if (quantity < 1) {
            throw new BusinessException("Số lượng phải từ 1 trở lên.");
        }
        inTransaction(em -> {
            Product p = em.find(Product.class, productId);
            if (!ProductRules.isVisibleToCustomer(p)) {
                throw new BusinessException("Sản phẩm không tồn tại.");
            }
            if (p.getStatus() != ProductStatus.ACTIVE) {
                throw new BusinessException("Sản phẩm \"" + p.getName() + "\" hiện "
                        + p.getStatus().getLabel().toLowerCase() + ".");
            }

            Cart cart = getOrCreateCart(em, customerId);
            CartItem item = cart.findItem(productId);
            int newQty = (item == null ? 0 : item.getQuantity()) + quantity;
            checkQuantity(p, newQty);

            if (item == null) {
                item = new CartItem();
                item.setProduct(p);
                item.setQuantity(newQty);
                cart.addItem(item);
                em.persist(item);
            } else {
                item.setQuantity(newQty);
            }
            cart.setUpdatedAt(LocalDateTime.now());
        });
    }

    /** Đổi số lượng. Số lượng 0 nghĩa là xóa khỏi giỏ. */
    public void updateQuantity(Long customerId, Long productId, int quantity) {
        if (quantity <= 0) {
            removeItem(customerId, productId);
            return;
        }
        inTransaction(em -> {
            Cart cart = findCart(em, customerId);
            CartItem item = cart == null ? null : cart.findItem(productId);
            if (item == null) {
                throw new BusinessException("Sản phẩm không có trong giỏ hàng.");
            }
            checkQuantity(item.getProduct(), quantity);
            item.setQuantity(quantity);
            cart.setUpdatedAt(LocalDateTime.now());
        });
    }

    public void removeItem(Long customerId, Long productId) {
        inTransaction(em -> {
            Cart cart = findCart(em, customerId);
            CartItem item = cart == null ? null : cart.findItem(productId);
            if (item != null) {
                cart.getItems().remove(item);   // orphanRemoval => DELETE
                cart.setUpdatedAt(LocalDateTime.now());
            }
        });
    }

    public void clear(Long customerId) {
        inTransaction(em -> {
            Cart cart = findCart(em, customerId);
            if (cart != null) {
                cart.getItems().clear();
                cart.setUpdatedAt(LocalDateTime.now());
            }
        });
    }

    // ---------- Hàm hỗ trợ ----------

    private void checkQuantity(Product p, int qty) {
        if (qty > MAX_QTY_PER_ITEM) {
            throw new BusinessException("Mỗi sản phẩm chỉ mua tối đa " + MAX_QTY_PER_ITEM + " cái.");
        }
        if (qty > p.getStockQuantity()) {
            throw new BusinessException("\"" + p.getName() + "\" chỉ còn "
                    + p.getStockQuantity() + " sản phẩm trong kho.");
        }
    }

    private Cart findCart(EntityManager em, Long customerId) {
        return em.createQuery(
                        "SELECT DISTINCT c FROM Cart c "
                                + "LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.product "
                                + "WHERE c.customerId = :cid", Cart.class)
                .setParameter("cid", customerId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    private Cart getOrCreateCart(EntityManager em, Long customerId) {
        Cart cart = findCart(em, customerId);
        if (cart == null) {
            cart = new Cart(customerId);
            em.persist(cart);
        }
        return cart;
    }

    /** Chạy đoạn code trong 1 transaction; có lỗi thì rollback. */
    private void inTransaction(Consumer<EntityManager> work) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            work.accept(em);
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
}
