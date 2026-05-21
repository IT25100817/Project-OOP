package com.example.grocery.config;

import jakarta.servlet.http.HttpSession;

public final class SessionUtil {
    private SessionUtil() {
    }

    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    public static boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }

    public static boolean isCustomer(HttpSession session) {
        return "CUSTOMER".equals(session.getAttribute("role"));
    }

    public static String getCurrentUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId == null ? "" : userId.toString();
    }

    public static String getCurrentCustomerType(HttpSession session) {
        Object customerType = session.getAttribute("customerType");
        return customerType == null ? "REGULAR" : customerType.toString();
    }

    public static String getOrCreateGuestId(HttpSession session) {
        Object guestId = session.getAttribute("guestId");
        if (guestId != null) {
            return guestId.toString();
        }
        String generated = "GUEST" + (System.currentTimeMillis() % 1000000);
        session.setAttribute("guestId", generated);
        session.setAttribute("customerType", "GUEST");
        return generated;
    }
}
