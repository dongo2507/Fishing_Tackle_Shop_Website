package com.fishing.servlet;

import com.fishing.dao.AddressDAO;
import com.fishing.model.Address;
import com.fishing.model.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/addresses")
public class AddressServlet extends HttpServlet {

    private final AddressDAO addressDAO = new AddressDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Customer customer = getLoggedInCustomer(request);

        if (customer == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        loadAddresses(request, customer);
        request.getRequestDispatcher("/address.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Customer customer = getLoggedInCustomer(request);

        if (customer == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String customerId = customer.getCustomerId();

        try {
            if ("add".equals(action)) {
                Address address = readAddress(request);
                addressDAO.add(address, customerId);
                redirectWithMessage(request, response,
                        "Thêm địa chỉ thành công.");

            } else if ("update".equals(action)) {
                Address address = readAddress(request);
                address.setId(request.getParameter("id"));
                boolean updated = addressDAO.update(address, customerId);
                redirectWithMessage(request, response,
                        updated ? "Cập nhật địa chỉ thành công."
                                : "Không tìm thấy địa chỉ cần sửa.");

            } else if ("delete".equals(action)) {
                boolean deleted = addressDAO.delete(
                        request.getParameter("id"), customerId);
                redirectWithMessage(request, response,
                        deleted ? "Xóa địa chỉ thành công."
                                : "Không tìm thấy địa chỉ cần xóa.");

            } else if ("default".equals(action)) {
                boolean updated = addressDAO.setDefault(
                        request.getParameter("id"), customerId);
                redirectWithMessage(request, response,
                        updated ? "Đã cập nhật địa chỉ mặc định."
                                : "Không tìm thấy địa chỉ.");
            } else {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Thao tác không hợp lệ.");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            loadAddresses(request, customer);
            request.getRequestDispatcher("/address.jsp")
                   .forward(request, response);
        } catch (RuntimeException e) {
            log("Lỗi xử lý địa chỉ giao hàng.", e);
            request.setAttribute(
                    "error", "Không thể xử lý địa chỉ. Vui lòng thử lại.");
            loadAddresses(request, customer);
            request.getRequestDispatcher("/address.jsp")
                   .forward(request, response);
        }
    }

    private Customer getLoggedInCustomer(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute("customer");
        if (value instanceof Customer) {
            return (Customer) value;
        }
        return null;
    }
    private void loadAddresses(HttpServletRequest request,
                               Customer customer) {
        List<Address> addresses =
                addressDAO.findByCustomer(customer.getCustomerId());
        request.setAttribute("addresses", addresses);
    }

    private Address readAddress(HttpServletRequest request) {
        String receiverName = request.getParameter("receiverName");
        String receiverPhone = request.getParameter("receiverPhone");
        String addressLabel = request.getParameter("addressLabel");
        String street = request.getParameter("street");
        String ward = request.getParameter("ward");
        String district = request.getParameter("district");
        String city = request.getParameter("city");

        receiverName = trim(receiverName);
        receiverPhone = trim(receiverPhone);
        addressLabel = trim(addressLabel);
        street = trim(street);
        ward = trim(ward);
        district = trim(district);
        city = trim(city);

        if (isBlank(receiverName) || isBlank(receiverPhone)
                || isBlank(street) || isBlank(ward)
                || isBlank(district) || isBlank(city)) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập đầy đủ thông tin địa chỉ.");
        }
        if (!receiverPhone.matches("[0-9]{10,15}")) {
            throw new IllegalArgumentException(
                    "Số điện thoại phải gồm 10 đến 15 chữ số.");
        }
        Address address = new Address();
        address.setReceiverName(receiverName);
        address.setReceiverPhone(receiverPhone);
        address.setAddressLabel(addressLabel);
        address.setStreet(street);
        address.setWard(ward);
        address.setDistrict(district);
        address.setCity(city);
        address.setDefault(request.getParameter("isDefault") != null);
        return address;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isEmpty();
    }

    private void redirectWithMessage(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String message)
            throws IOException {
        request.getSession().setAttribute("addressMessage", message);
        response.sendRedirect(
                request.getContextPath() + "/addresses");
    }
}