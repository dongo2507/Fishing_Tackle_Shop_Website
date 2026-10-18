# Giỏ hàng & Đơn hàng – phần của An

Mô-đun: **Cart, CartItem, Order, LineItem** (+ enum `OrderStatus`).
Viết theo đúng cách của phần Đô: JPA/Hibernate + Servlet + JSP, cùng `JPAUtil`, `TempAuth`, flash message, CSS chung.

## 1. Danh sách file

| Loại | File | Việc chính |
|---|---|---|
| Enum | `enums/OrderStatus.java` | PENDING → CONFIRMED → SHIPPING → SUCCESS, hoặc CANCELLED. Có `next()`, `isCancellable()`, `isFinal()` |
| Entity | `model/Cart.java` | 1 khách = 1 giỏ (lưu DB). Có `calculateTotal()` |
| Entity | `model/CartItem.java` | 1 dòng giỏ. **Không lưu giá**, luôn lấy giá hiện tại của Product |
| Entity | `model/Order.java` | Bảng `orders`. Lưu snapshot người nhận + tiền (tạm tính, giảm giá, phí ship, tổng) |
| Entity | `model/LineItem.java` | 1 dòng đơn. **Chốt `unitPrice` và `productName`** lúc đặt |
| DAO | `dao/CartDAO.java` | Thêm / đổi số lượng / xóa / xóa hết, đếm số lượng |
| DAO | `dao/OrderDAO.java` | `placeOrder` (giỏ → đơn), xem đơn, tìm đơn, `advanceStatus`, `cancelOrder` |
| Servlet | `servlet/CartServlet.java` | `/cart` |
| Servlet | `servlet/CheckoutServlet.java` | `/checkout` |
| Servlet | `servlet/OrderServlet.java` | `/orders` (khách) |
| Servlet | `servlet/AdminOrderServlet.java` | `/admin/orders` (admin) |
| Filter | `filter/CartCountFilter.java` | Đặt số lượng giỏ lên thanh menu |
| Util | `util/BusinessException.java`, `util/WebUtil.java` | Lỗi nghiệp vụ có thông báo; hàm parse/flash dùng chung |
| JSP | `views/customer/cart/view.jsp` | Trang giỏ hàng |
| JSP | `views/customer/order/checkout.jsp`, `list.jsp`, `detail.jsp` | Đặt hàng, lịch sử, chi tiết đơn |
| JSP | `views/admin/order/list.jsp`, `detail.jsp` | Admin quản lý đơn |
| JSP | `views/common/order-info.jsp`, `order-items.jsp` | Phần dùng chung khách/admin |
| CSS | `css/order.css` | Giao diện giỏ hàng, đơn hàng |

**File của người khác có sửa nhẹ** (báo nhóm biết khi gộp code):
- `persistence.xml`: thêm 4 dòng `<class>` cho Cart, CartItem, Order, LineItem.
- `common/shop-header.jsp`: thêm menu "Đơn hàng của tôi", "Giỏ hàng (số)", nạp `order.css` và `main.js`.
- `common/header.jsp`: thêm menu "Đơn hàng", nạp `order.css`.
- `customer/product/detail.jsp`: thay dòng chú thích "Nút thêm vào giỏ hàng sẽ ghép với phần giỏ hàng của An" bằng form **Thêm vào giỏ / Mua ngay**.

## 2. Đối chiếu yêu cầu MVP

| Yêu cầu | Làm ở đâu |
|---|---|
| Thêm sản phẩm, đổi số lượng, xóa khỏi giỏ | `CartDAO.addItem / updateQuantity / removeItem / clear` |
| Tự tính tạm tính `calculateTotal` | `Cart.calculateTotal()` (chỉ cộng các món còn mua được) |
| Lưu giỏ theo Session/Database | Lưu **database** (bảng `cart`, `cart_item`), theo `customerId` |
| Chuyển giỏ → đơn mới | `OrderDAO.placeOrder()` |
| LineItem giữ giá cố định lúc mua | `LineItem.fromCartItem()` copy `price` → `unitPrice` |
| Khách xem lịch sử đơn | `/orders`, `/orders?id=` |
| Admin quản lý + chuyển trạng thái | `/admin/orders` (tab lọc, tìm theo mã/SĐT/tên, nút chuyển bước) |
| Hủy đơn `cancelOrder` | `OrderDAO.cancelOrder()` – hoàn kho trong transaction |

## 3. Những điểm hay bị hỏi khi bảo vệ

**Vì sao giỏ hàng không lưu giá mà LineItem lại lưu giá?**
Giỏ hàng chỉ là "ý định mua", nên luôn hiện giá mới nhất. Khi đặt hàng thì giá phải chốt lại: sau này Admin đổi giá, đơn cũ vẫn đúng số tiền khách đã trả. `productName` và `thumbnailUrl` cũng được chốt vì lý do tương tự.

**Đặt hàng diễn ra thế nào? (`placeOrder`)** — tất cả trong 1 transaction:
1. Khóa dòng giỏ hàng (`SELECT ... FOR UPDATE`) → bấm "Đặt hàng" 2 lần thì lần 2 phải chờ, sau đó thấy giỏ trống và báo lỗi, không tạo 2 đơn.
2. Khóa các sản phẩm trong giỏ theo thứ tự id tăng dần (tránh deadlock khi 2 người đặt cùng lúc).
3. Kiểm tra từng món: còn bán (`ACTIVE`, không bị ẩn) và đủ tồn kho.
4. Tạo LineItem, trừ `stockQuantity`; về 0 thì chuyển sản phẩm sang `OUT_OF_STOCK`.
5. Tính tiền, lưu Order (cascade lưu LineItem), xóa giỏ.
Lỗi ở bước nào → `BusinessException` → rollback toàn bộ, kho không bị trừ, quay về giỏ kèm thông báo.

**Vì sao phải khóa sản phẩm?** Không khóa thì 2 người cùng đọc "còn 1 cái", cùng trừ → kho thành -1 (lỗi race condition / lost update).

**Hủy đơn (`cancelOrder`)**:
- Khách chỉ tự hủy được khi đơn còn **Chờ xác nhận**, và chỉ đơn của chính mình.
- Admin hủy được khi **Chờ xác nhận** hoặc **Đã xác nhận** (hàng chưa rời kho).
- Hoàn `quantity` của từng LineItem về `Product.stockQuantity` trong cùng transaction; sản phẩm đang `OUT_OF_STOCK` mà có hàng lại thì mở bán (`ACTIVE`). Sản phẩm bị Ẩn/Ngừng kinh doanh thì giữ nguyên.

**Luồng trạng thái**: PENDING → CONFIRMED → SHIPPING → SUCCESS. Admin chỉ bấm được bước kế tiếp (`OrderStatus.next()`), không nhảy cóc hay quay lui. SUCCESS và CANCELLED là trạng thái kết thúc.

**Bảo mật đã có**: JPQL dùng tham số (`:cid`, `:kw`) chống SQL Injection; JSP dùng `<c:out>` chống XSS; khách xem đơn người khác → "Không tìm thấy đơn hàng"; kiểm tra dữ liệu ở server (SĐT 10 số bắt đầu bằng 0, địa chỉ ≥ 10 ký tự, số lượng 1–99 và ≤ tồn kho).

**Bảng `orders` chứ không phải `order`** vì ORDER là từ khóa SQL.

## 4. Chỗ chờ ghép với phần của bạn khác

| Phần | Hiện tại | Khi ghép |
|---|---|---|
| Dũng – đăng nhập | Dùng `TempAuth.getCustomerId()` (mặc định khách #1) | Dũng sửa `TempAuth` là xong, code giỏ/đơn không phải đổi. Nên chặn `/cart`, `/checkout`, `/orders` khi chưa đăng nhập và `/admin/*` khi không phải admin |
| Dũng – Address | Checkout nhập tay họ tên, SĐT, địa chỉ | Cho chọn Address có sẵn rồi copy vào 3 cột snapshot của Order |
| Dũng – Promotion | `discountAmount = 0` | Kiểm tra mã ở checkout → set `discountAmount` trước `order.recalculate()` |
| Lộc – Payment, Shipping | **Đã ghép xong** | Xem `HUONG_DAN_THANH_TOAN_VAN_CHUYEN.md` |
| Dũng – Notification | Chưa có | Gọi tạo thông báo trong `placeOrder`, `advanceStatus`, `cancelOrder` |

## 5. Chạy thử

1. Chạy như cũ (PostgreSQL `fishing_shop`, Smart Tomcat). Hibernate `hbm2ddl=update` tự tạo 4 bảng mới: `cart`, `cart_item`, `orders`, `line_item`.
2. Admin vào **Sản phẩm** tạo 1–2 sản phẩm trạng thái "Đang bán", tồn kho > 0.
3. Mở `/product?id=1` → **Thêm vào giỏ** hoặc **Mua ngay**.
4. `/cart` → đổi số lượng, xóa → **Tiến hành đặt hàng** → điền thông tin → **Đặt hàng**.
5. Kiểm tra tồn kho sản phẩm đã giảm ở trang Admin.
6. `/orders` xem lịch sử; thử **Hủy đơn** → tồn kho tăng lại.
7. `/admin/orders` → bấm chuyển trạng thái từng bước; thử hủy ở trang chi tiết.

Các trường hợp nên thử thêm: thêm quá tồn kho; Admin đổi sản phẩm sang "Ẩn" khi nó đang trong giỏ (giỏ báo vàng, không cho đặt); Admin đổi giá sau khi đặt (đơn cũ giữ giá cũ).
