package com.fishing.enums;

public enum ProductStatus {
    ACTIVE("Đang bán"),
    OUT_OF_STOCK("Hết hàng"),
    DISCONTINUED("Ngừng kinh doanh"),
    HIDDEN("Ẩn");

    private final String label;

    ProductStatus(String label) {
        this.label = label;
    }

    /** Tên hiển thị tiếng Việt, dùng trong JSP: ${s.label} */
    public String getLabel() {
        return label;
    }
}
