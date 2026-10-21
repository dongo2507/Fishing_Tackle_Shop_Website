<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.fishing.model.Address"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Địa chỉ giao hàng - Fishing Tackle Shop</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/address.css">
</head>
<body>
    <div class="container">
        <h2>Địa chỉ giao hàng</h2>
        <% if (request.getAttribute("message") != null) { %>
            <p class="message">
                <%= request.getAttribute("message") %>
            </p>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <p class="error">
                <%= request.getAttribute("error") %>
            </p>
        <% } %>

        <h3>Địa chỉ đã lưu</h3>
        <%
            List<Address> addresses =
                    (List<Address>) request.getAttribute("addresses");
        %>
        <% if (addresses == null || addresses.isEmpty()) { %>
            <p>Bạn chưa có địa chỉ giao hàng nào.</p>
        <% } else { %>
            <% for (Address address : addresses) { %>
                <div class="address-card">
                    <p>
                        <strong><%= address.getReceiverName() %></strong>
                        - <%= address.getReceiverPhone() %>
                    </p>

                    <p>
                        <%= address.getStreet() %>,
                        <%= address.getWard() %>,
                        <%= address.getDistrict() %>,
                        <%= address.getCity() %>
                    </p>
                    <p>
                        Loại địa chỉ:
                        <%= address.getAddressLabel() == null
                                ? "Chưa phân loại"
                                : address.getAddressLabel() %>
                    </p>
                    <% if (address.isDefault()) { %>
                        <p class="default-address">Địa chỉ mặc định</p>
                    <% } %>
                    <div class="address-actions">
                        <button type="button"
                                onclick="editAddress(
                                    '<%= address.getId() %>',
                                    '<%= address.getReceiverName() %>',
                                    '<%= address.getReceiverPhone() %>',
                                    '<%= address.getAddressLabel() == null
                                            ? "" : address.getAddressLabel() %>',
                                    '<%= address.getStreet() %>',
                                    '<%= address.getWard() %>',
                                    '<%= address.getDistrict() %>',
                                    '<%= address.getCity() %>',
                                    <%= address.isDefault() %>
                                )">
                            Sửa
                        </button>

                        <% if (!address.isDefault()) { %>
                            <form action="${pageContext.request.contextPath}/addresses"
                                  method="post">
                                <input type="hidden" name="action" value="default">
                                <input type="hidden" name="id"
                                       value="<%= address.getId() %>">
                                <button type="submit">Đặt làm mặc định</button>
                            </form>
                        <% } %>

                        <form action="${pageContext.request.contextPath}/addresses"
                              method="post"
                              onsubmit="return confirm('Bạn có chắc muốn xóa địa chỉ này?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id"
                                   value="<%= address.getId() %>">
                            <button type="submit">Xóa</button>
                        </form>
                    </div>
                </div>
            <% } %>

        <% } %>

        <hr>

        <h3 id="form-title">Thêm địa chỉ mới</h3>

        <form action="${pageContext.request.contextPath}/addresses"
              method="post" id="address-form">

            <input type="hidden" name="action" id="action" value="add">
            <input type="hidden" name="id" id="addressId">

            <label for="receiverName">Họ và tên người nhận</label>
            <input type="text" id="receiverName" name="receiverName"
                   maxlength="100" required>

            <label for="receiverPhone">Số điện thoại</label>
            <input type="tel" id="receiverPhone" name="receiverPhone"
                   pattern="[0-9]{10,15}"
                   title="Nhập số điện thoại gồm 10 đến 15 chữ số"
                   required>

            <label for="addressLabel">Loại địa chỉ</label>
            <select id="addressLabel" name="addressLabel">
                <option value="Nhà riêng">Nhà riêng</option>
                <option value="Văn phòng">Văn phòng</option>
            </select>

            <label for="street">Địa chỉ cụ thể</label>
            <input type="text" id="street" name="street"
                   placeholder="Số nhà, tên đường..." required>

            <label for="ward">Phường/Xã</label>
            <input type="text" id="ward" name="ward" required>

            <label for="district">Quận/Huyện</label>
            <input type="text" id="district" name="district" required>

            <label for="city">Tỉnh/Thành phố</label>
            <input type="text" id="city" name="city" required>

            <label class="checkbox-label">
                <input type="checkbox" id="isDefault" name="isDefault">
                Đặt làm địa chỉ mặc định
            </label>

            <button type="submit" id="submit-button">Lưu địa chỉ</button>
            <button type="button" id="cancel-button"
                    onclick="resetForm()" hidden>
                Hủy sửa
            </button>
        </form>

        <div class="back">
            <a href="${pageContext.request.contextPath}/">Quay lại trang chủ</a>
        </div>
    </div>

    <script>
        function setValue(id, value) {
            document.getElementById(id).value = value || "";
        }

        function editAddress(id, name, phone, label, street,
                             ward, district, city, isDefault) {
            setValue("action", "update");
            setValue("addressId", id);
            setValue("receiverName", name);
            setValue("receiverPhone", phone);
            setValue("addressLabel", label);
            setValue("street", street);
            setValue("ward", ward);
            setValue("district", district);
            setValue("city", city);

            document.getElementById("isDefault").checked = isDefault;
            document.getElementById("form-title").textContent = "Sửa địa chỉ";
            document.getElementById("submit-button").textContent = "Cập nhật địa chỉ";
            document.getElementById("cancel-button").hidden = false;

            document.getElementById("address-form").scrollIntoView({
                behavior: "smooth"
            });
        }

        function resetForm() {
            document.getElementById("address-form").reset();
            setValue("action", "add");
            setValue("addressId", "");
            document.getElementById("form-title").textContent = "Thêm địa chỉ mới";
            document.getElementById("submit-button").textContent = "Lưu địa chỉ";
            document.getElementById("cancel-button").hidden = true;
        }
    </script>
</body>
</html>