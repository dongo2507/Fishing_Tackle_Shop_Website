# Hướng dẫn giao diện chung (cả 4 thành viên)

Mọi trang của Đô, An, Lộc, Dũng dùng **một bộ giao diện duy nhất**.

## 1. Cấu trúc giao diện

| File | Vai trò | Ai sửa |
|---|---|---|
| `css/style.css` | Biến màu, nút, bảng, form, nhãn, thông báo, khung Admin, thẻ đăng nhập, khu tài khoản | Cả nhóm, sửa cẩn thận (báo trong nhóm) |
| `css/shop.css` | Header khách, trang chủ, thẻ sản phẩm, chi tiết sản phẩm, đánh giá | Đô |
| `css/order.css` | Giỏ hàng, đơn hàng | An |
| `css/payment.css` | Thanh toán, vận chuyển | Lộc |
| `css/notification.css`, `js/notification.js` | Chuông thông báo | Dũng |
| `js/ui.js` | Nút ☰ trên điện thoại, thông báo thành công tự ẩn | Cả nhóm |
| `common/assets.jsp` | **Nơi duy nhất** khai báo CSS/JS (cả khung Admin và khung khách include file này) | Cả nhóm |
| `common/header.jsp` + `footer.jsp` | Khung **Admin** (menu bên trái) | Không sửa, chỉ sửa menu |
| `common/shop-header.jsp` | Khung **khách hàng** | Không sửa, chỉ sửa menu |
| `common/admin-menu.jsp` | Các mục menu Admin, chia nhóm theo người làm | Mỗi người sửa nhóm của mình |
| `common/shop-menu.jsp` | Các mục menu khách | Mỗi người thêm mục của mình |
| `common/user-menu.jsp` | Chuông thông báo + Đăng nhập/Đăng ký/Đăng xuất | **Dũng** |

## 2. Quy tắc bắt buộc

1. Mỗi trang Admin bắt đầu bằng `<jsp:include page="/WEB-INF/views/common/header.jsp">` (kèm `<jsp:param name="title" .../>`) và kết thúc bằng `common/footer.jsp`.
2. Mỗi trang khách bắt đầu bằng `common/shop-header.jsp` và kết thúc bằng `common/footer.jsp`.
3. **Không** tự viết `<html>`, `<head>`, `<link rel="stylesheet">` trong trang. Muốn thêm CSS/JS riêng, thêm một dòng vào `common/assets.jsp`.
4. **Không** gõ màu cứng (`#0b5ea8`...). Dùng biến: `var(--primary)`, `var(--danger)`, `var(--border)`, `var(--radius)`...
5. Mọi đường dẫn có `${ctx}` (`${pageContext.request.contextPath}`), không gõ cứng `/css/...`.
6. Dữ liệu người dùng luôn qua `<c:out>`.
7. Không dùng từ khóa Java làm tên thuộc tính trong EL (`${x.final}`, `${x.class}`...): Tomcat 11 sẽ báo lỗi 500.
8. Hành động ghi dữ liệu (xóa, hủy, đổi trạng thái) dùng `<form method="post">`, nút xóa thêm `data-confirm="..."` trên thẻ form để hiện hộp xác nhận.

## 3. Thành phần có sẵn (class)

| Nhóm | Class |
|---|---|
| Bố cục | `.container`, `.page-head`, `.panel`, `.panel-wide`, `.section`, `.two-col`, `.info-grid`, `.mt` |
| Nút | `.btn`, `.btn-primary`, `.btn-accent`, `.btn-buy`, `.btn-danger`, `.btn-sm`, `.btn-block`, `.disabled` |
| Bảng | `.table`, `.num` (căn phải), `.actions`, `.empty`, `.tabs` |
| Form | `.form`, `.form-row` (2 cột), `.form-actions`, `.req`, `.field-error`, `.hint`, `.filter-bar` |
| Nhãn | `.badge .badge-on/.badge-off`, `.status .status-PENDING...`, `.pay-status .pay-*`, `.ship-status .ship-*` |
| Thông báo | `.alert .alert-success/.alert-error` (thành công tự ẩn sau 6 giây) |
| Sản phẩm | `<t:productCard product="${p}" rating="${ratings[p.id]}"/>` (thẻ dùng chung), `.grid`, `.stars` |
| Đăng nhập | `.auth-wrap`, `.auth-card`, `.auth-sub`, `.auth-foot` |
| Tài khoản | `.account-layout`, `.account-nav`, `.address-card`, `.address-card.default`, `.address-actions` |

Để dùng thẻ sản phẩm trong JSP: `<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>`.

## 4. Dành cho Dũng

1. Mẫu trang trong `docs/mau-giao-dien-dung/`: `login.jsp`, `register.jsp`, `address-list.jsp`. Chép vào `WEB-INF/views/customer/...`, đổi tên biến cho khớp servlet của bạn.
2. Khi trang đăng nhập đã chạy: đổi `authEnabled` trong `WEB-INF/web.xml` thành `true` thì header khách tự hiện Đăng nhập / Đăng ký / Đăng xuất.
3. Sau khi đăng nhập thành công, đặt vào session: `currentUserName` (String), `customerId` (Long), `role` ("CUSTOMER" hoặc "ADMIN"). Sửa 2 hàm trong `AuthUtil` để lấy id từ đó.
4. Trang Admin của bạn (khách hàng, khuyến mãi): dùng `.table`, `.form`, `.filter-bar` như trang sản phẩm; thêm link vào nhóm "Người dùng" trong `admin-menu.jsp` (bỏ dấu chú thích sẵn có).
5. Danh sách thông báo thật: thay mảng mẫu trong `js/notification.js` bằng dữ liệu từ servlet, **giữ nguyên các id** trong `user-menu.jsp`.

## 5. Đổi tông màu cả website

Sửa các biến trong khối `:root` ở đầu `css/style.css` (ví dụ `--primary`, `--navy`, `--accent`). Không cần sửa trang nào.
