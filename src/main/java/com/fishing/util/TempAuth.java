package com.fishing.util;

import com.fishing.model.Admin;
import jakarta.servlet.http.HttpServletRequest;

public final class TempAuth {

    private static final Long DEFAULT_CUSTOMER_ID = 1L;

    private TempAuth() {}

    public static Long getCustomerId(HttpServletRequest req) {
        Object v = req.getSession().getAttribute("customerId");
        return v instanceof Long ? (Long) v : DEFAULT_CUSTOMER_ID;
    }

    public static String getAdminId(HttpServletRequest req) {
        Object v = req.getSession().getAttribute("admin");

        if (v instanceof Admin) {
            return ((Admin) v).getId();
        }

        return null;
    }
}