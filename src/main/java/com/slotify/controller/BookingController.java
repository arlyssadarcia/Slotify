package com.slotify.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slotify.model.User;
import com.slotify.service.BookingService;
import com.slotify.service.ResourceService;

import jakarta.servlet.http.HttpSession;

@Controller 
public class BookingController {

    private final BookingService bookingService;
    private final ResourceService resourceService;

    public BookingController(BookingService bookingService, ResourceService resourceService) {
        this.bookingService = bookingService;
        this.resourceService = resourceService;
    }

    private User requireUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            throw new IllegalStateException("NOT_LOGGED_IN");
        }
        return user;
    }

    @GetMapping("/bookings")
    public String myBookings(@RequestParam(required = false) String ok,
                             HttpSession session, 
                             Model model) {
        try {
            User user = requireUser(session);
            model.addAttribute("user", user);
            model.addAttribute("bookings", bookingService.myBookings(user));
            if ("requested".equals(ok)) {
                model.addAttribute("success", "Booking request submitted. Waiting for admin approval.");
            } else if ("cancelled".equals(ok)) {
                model.addAttribute("success", "Booking request cancelled.");
            }
            return "bookings";
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }
    }

    @GetMapping("/bookings/new")
    public String newBooking(@RequestParam Long resourceId,
                             HttpSession session,
                             Model model) {
        try {
            User user = requireUser(session);
            model.addAttribute("user", user);
            model.addAttribute("resource", resourceService.getById(resourceId));
            return "booking-form";
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        }
    }

    @PostMapping("/bookings")
    public String create(@RequestParam Long resourceId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
                         @RequestParam(required = false) String purpose,
                         HttpSession session,
                         Model model) {
        try {
            User user = requireUser(session);
            bookingService.requestBooking(user, resourceId, startAt, endAt, purpose);
            return "redirect:/bookings?ok=requested";
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            User user = (User) session.getAttribute("currentUser");
            model.addAttribute("user", user);
            model.addAttribute("resource", resourceService.getById(resourceId));
            model.addAttribute("error", ex.getMessage());
            return "booking-form";
        }
    }

    @PostMapping("/bookings/cancel")
    public String cancel(@RequestParam Long bookingId, HttpSession session) {
        try {
            User user = requireUser(session);
            bookingService.cancel(bookingId, user);
            return "redirect:/bookings?ok=cancelled";
        } catch (IllegalStateException ex) {
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            return "redirect:/bookings";
        }
    }

    @GetMapping("/admin/bookings")
    public String adminPending(@RequestParam(required = false) String ok, 
                               HttpSession session, 
                               Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(user.getRole())) {
            return "redirect:/home";
        }
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookingService.byStatus("PENDING"));
        if ("approved".equals(ok)) {
            model.addAttribute("success", "Booking approved.");
        } else if ("rejected".equals(ok)) {
            model.addAttribute("success", "Booking rejected.");
        }
        return "admin-bookings";
    }

    @PostMapping("/admin/bookings/approve")
    public String approve(@RequestParam Long bookingId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(user.getRole())) {
            return "redirect:/home";
        }
        try {
        bookingService.approve(bookingId);
        return "redirect:/admin/bookings?ok=approved";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("user", user);
            model.addAttribute("bookings", bookingService.byStatus("PENDING"));
            model.addAttribute("error", ex.getMessage());
            return "admin-bookings";
        }
    }

    @PostMapping("/admin/bookings/reject")
    public String reject(@RequestParam Long bookingId,
                         @RequestParam(required = false) String reason,
                         HttpSession session,
                         Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(user.getRole())) {
            return "redirect:/home";
        }
        try {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("Please enter a reject reason");
            }
            bookingService.reject(bookingId, reason.trim());
            return "redirect:/admin/bookings?ok=rejected";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("user", user);
            model.addAttribute("bookings", bookingService.byStatus("PENDING"));
            model.addAttribute("error", ex.getMessage());
            return "admin-bookings";
        }    
    }
}
