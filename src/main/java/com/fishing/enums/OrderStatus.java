package com.fishing.enums;
public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    SHIPPING("Đang giao"),
    SUCCESS("Giao thành công"),
    CANCELLED("Đã hủy");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    /** Tên hiển thị tiếng Việt, dùng trong JSP: ${o.status.label} */
    public String getLabel() {
        return label;
    }

    /** Trạng thái kết thúc thì không đổi được nữa. */
    public boolean isFinal() {
        return this == SUCCESS || this == CANCELLED;
    }

    /** Chỉ hủy được khi hàng chưa rời kho. */
    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED;
    }
    public OrderStatus next() {
        switch (this) {
            case PENDING:   return CONFIRMED;
            case CONFIRMED: return SHIPPING;
            case SHIPPING:  return SUCCESS;
            default:        return null;
        }
    }
}
