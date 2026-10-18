# Thanh toán & Vận chuyển – phần của Lộc

Mô-đun: **Payment, Shipping, PaymentMethod, PaymentStatus** (+ enum `ShippingStatus`).
Cùng kiểu với cả nhóm: JPA/Hibernate + Servlet + JSP. Nối thẳng vào luồng đặt hàng của An.

## 1. Danh sách file

| Loại | File | Việc chính |
|---|---|---|
| Enum | `enums/PaymentMethod.java` | COD, BANK_TRANSFER (đang bật); VNPAY, MOMO (để dành nâng cao, khách chưa chọn được) |
| Enum | `enums/PaymentStatus.java` | PENDING, SUCCESS, FAILED, REFUNDED |
| Enum | `enums/ShippingStatus.java` | PREPARING (Chờ lấy hàng), SHIPPED, DELIVERED, CANCELLED |
| Entity | `model/Payment.java` | Bảng `payment`: order (1-1), amount, method, status, paymentDate, transactionRef |
| Entity | `model/Shipping.java` | Bảng `shipping`: order (1-1), địa chỉ, carrierName, trackingNumber, shippingFee, status, ngày giao dự kiến, shippedDate, deliveredDate |
| Config | `util/ShopConfig.java` | Phí ship, ngưỡng miễn phí, số ngày giao dự kiến, danh sách đơn vị vận chuyển, **tài khoản ngân hàng** |
| DAO | `dao/PaymentDAO.java` | Tạo thanh toán, xác nhận chuyển khoản, cập nhật khi giao / hủy, lọc danh sách |
| DAO | `dao/ShippingDAO.java` | Tạo vận đơn, cập nhật ĐVVC + mã vận đơn + ngày dự kiến, cập nhật khi giao / hủy, tìm kiếm |
| Servlet | `servlet/AdminPaymentServlet.java` | `/admin/payments`: danh sách + nút "Xác nhận đã nhận tiền" |
| Servlet | `servlet/AdminShippingServlet.java` | `/admin/shippings`: danh sách + lưu thông tin vận chuyển |
| JSP | `views/admin/payment/list.jsp`, `views/admin/shipping/list.jsp` | 2 trang quản lý riêng |
| CSS | `css/payment.css` | Giao diện thanh toán / vận chuyển |

**Chỗ ghép vào file của người khác:**
- `OrderDAO` (An): `placeOrder` tính phí ship, tạo Payment + Shipping; `advanceStatus` và `cancelOrder` gọi các hàm cập nhật của Lộc. Mỗi chỗ có chú thích "phần của Lộc".
- `CheckoutServlet` + `checkout.jsp` (An): chọn phương thức thanh toán, hiện phí ship và tổng tiền.
- `OrderServlet`, `AdminOrderServlet` + 2 trang chi tiết đơn (An): thêm khung **Thanh toán** và **Vận chuyển**.
- `persistence.xml`, `header.jsp`, `shop-header.jsp`: khai báo entity, thêm menu Thanh toán / Vận chuyển, nạp `payment.css`.

## 2. Đối chiếu yêu cầu MVP

| Yêu cầu | Làm ở đâu |
|---|---|
| Hỗ trợ COD và Chuyển khoản | Khách chọn ở trang đặt hàng; chuyển khoản thì trang chi tiết đơn hiện số tài khoản + nội dung `DH000012` |
| Ghi nhận giao dịch Payment liên kết Order | `PaymentDAO.createForOrder()` chạy trong transaction đặt hàng |
| Cập nhật & truy xuất PaymentStatus | Tự đổi theo đơn (xem mục 3) + Admin xác nhận chuyển khoản; trang `/admin/payments` lọc theo phương thức, trạng thái |
| Tạo thông tin vận chuyển (địa chỉ, ĐVVC, mã vận đơn) | Tạo tự động khi đặt hàng; Admin nhập ĐVVC + mã vận đơn ở chi tiết đơn |
| Admin cập nhật tiến trình + ngày giao dự kiến/thực tế | Ngày dự kiến: Admin sửa được (mặc định +3 ngày). Ngày giao cho ĐVVC và ngày giao thực tế: tự ghi khi đơn chuyển trạng thái |

## 3. Thanh toán & vận chuyển đi theo trạng thái đơn

| Đơn hàng (An) | Thanh toán | Vận chuyển | Điều kiện |
|---|---|---|---|
| Đặt hàng → **Chờ xác nhận** | tạo, PENDING | tạo, PREPARING; phí ship, ngày dự kiến = hôm nay + 3 | |
| → **Đã xác nhận** | – | – | |
| → **Đang giao** | – | SHIPPED, ghi `shippedDate` | Phải có ĐVVC + mã vận đơn; đơn chuyển khoản phải **đã nhận tiền** |
| → **Giao thành công** | COD: SUCCESS, ghi `paymentDate` | DELIVERED, ghi `deliveredDate` | |
| **Hủy đơn** | PENDING → FAILED; SUCCESS → REFUNDED | CANCELLED | |

Tất cả chạy **trong cùng transaction** với việc đổi trạng thái đơn. Ví dụ Admin bấm "Đang giao" mà chưa nhập mã vận đơn → `BusinessException` → rollback, đơn vẫn ở "Đã xác nhận" và hiện thông báo lỗi.

## 4. Những điểm hay bị hỏi khi bảo vệ

**Phí ship tính thế nào?** Đồng giá 30.000đ, miễn phí cho đơn từ 500.000đ (`ShopConfig.calculateShippingFee`). Phí được tính ở **server** lúc đặt hàng, không lấy từ form, nên khách không sửa được. Sau đó lưu vào cả `Order.shippingFee` lẫn `Shipping.shippingFee`.

**Vì sao `Payment.amount` lưu riêng mà không lấy từ Order?** Đó là số tiền của giao dịch tại thời điểm tạo. Sau này có thanh toán nhiều lần hay hoàn một phần thì vẫn tách bạch được.

**Vì sao đơn chuyển khoản phải nhận tiền mới được giao?** Giao trước thì khách có thể không trả tiền. COD thì ngược lại: nhân viên giao hàng thu tiền, nên khi giao thành công mới chuyển SUCCESS.

**Xác nhận chuyển khoản**: Admin xem sao kê, thấy khoản tiền có nội dung `DH000012`, nhập mã giao dịch ngân hàng vào `transactionRef` rồi bấm xác nhận. Server kiểm tra: đúng là chuyển khoản, đang PENDING, đơn chưa hủy. Mã giao dịch chỉ nhận chữ, số và `- _ .`.

**REFUNDED nghĩa là gì?** Đơn đã thanh toán rồi mới hủy. Hệ thống đánh dấu cần hoàn, còn việc chuyển tiền trả khách cửa hàng làm thủ công. Trang Admin có dòng nhắc màu cam.

**Kiểm tra dữ liệu ở server**: ĐVVC phải nằm trong danh sách có sẵn; mã vận đơn 6–30 ký tự, tự đổi chữ in hoa, không trùng với đơn khác; ngày dự kiến trong khoảng hợp lý; phương thức thanh toán chỉ nhận COD / BANK_TRANSFER (gửi giá trị lạ như `VNPAY` qua form sẽ bị từ chối).

**Quan hệ 1-1**: `payment.order_id` và `shipping.order_id` có ràng buộc `UNIQUE`, nên mỗi đơn chỉ có một thanh toán và một vận đơn.

## 5. Trước khi demo

1. Sửa `BANK_NAME`, `BANK_ACCOUNT_NO`, `BANK_ACCOUNT_NAME` trong `ShopConfig.java` (đang là số tài khoản giả).
2. Hibernate tự tạo thêm 2 bảng `payment`, `shipping`. Đơn đặt **trước** khi thêm phần này sẽ không có thanh toán / vận chuyển (trang hiện "Chưa có thông tin"). Nên xóa đơn test cũ hoặc tạo đơn mới để demo.

## 6. Kịch bản demo

**COD:** đặt đơn < 500k (thấy phí ship 30.000đ) → Admin: Xác nhận → nhập GHN + mã `GHN12345678` → Đang giao → Giao thành công → thanh toán tự thành "Đã thanh toán", có ngày giao thực tế.

**Chuyển khoản:** đặt đơn ≥ 500k (miễn phí ship), chọn Chuyển khoản → khách thấy số tài khoản → Admin bấm "Đang giao" bị chặn → nhập mã giao dịch, xác nhận → giao bình thường.

**Hủy:** hủy đơn chuyển khoản đã xác nhận tiền → thanh toán thành "Đã hoàn tiền", vận chuyển "Đã hủy", tồn kho được trả lại.

## 7. Hướng nâng cao (chưa làm)

- VNPay / MoMo sandbox: enum đã có sẵn, cần thêm servlet tạo URL thanh toán + servlet nhận callback (IPN) để chuyển Payment sang SUCCESS.
- API GHN / GHTK: thay `ShopConfig.calculateShippingFee` bằng gọi API tính phí, và đẩy đơn để lấy mã vận đơn tự động.
- Trang tra cứu vận đơn cho khách theo `trackingNumber`.
