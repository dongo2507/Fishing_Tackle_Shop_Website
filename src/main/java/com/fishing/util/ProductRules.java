package com.fishing.util;

import com.fishing.enums.ProductStatus;
import com.fishing.model.Category;
import com.fishing.model.Product;

/** Quy tắc hiển thị sản phẩm cho khách hàng. */
public final class ProductRules {

    private ProductRules() {}

    /** Khách chỉ thấy sản phẩm không bị ẩn và không thuộc danh mục đang ẩn. */
    public static boolean isVisibleToCustomer(Product p) {
        if (p == null || p.getStatus() == ProductStatus.HIDDEN) {
            return false;
        }
        Category c = p.getCategory();
        return c == null || c.isVisible();
    }
}
