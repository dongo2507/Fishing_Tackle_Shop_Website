package com.fishing.util;

/**
 * Lỗi nghiệp vụ có thông báo dành cho người dùng (ví dụ "Sản phẩm X chỉ còn 2 cái").
 * DAO ném lỗi này, Servlet bắt lại và hiển thị message lên trang.
 * Khi ném ra bên trong transaction, DAO sẽ rollback toàn bộ.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
