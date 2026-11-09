package vn.edu.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.cinema.dto.BookingCart;
import vn.edu.cinema.entity.Showtime;
import vn.edu.cinema.exception.BusinessException;
import vn.edu.cinema.service.BookingService;
import vn.edu.cinema.service.ShowtimeService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

/**
 * Luồng đặt vé trên web:
 *  1. Chọn ghế            GET/POST /showtimes/{id}/seats  -> lưu giỏ ghế vào HttpSession
 *  2. Xác nhận            GET/POST /booking/confirm       -> đọc giỏ từ HttpSession, tạo vé, xoá giỏ
 *  3. Vé của tôi / huỷ    GET /bookings, POST /bookings/{id}/cancel
 */
@Controller
public class BookingWebController {

    static final String CART_ATTR = "bookingCart";

    private final ShowtimeService showtimeService;
    private final BookingService bookingService;

    public BookingWebController(ShowtimeService showtimeService, BookingService bookingService) {
        this.showtimeService = showtimeService;
        this.bookingService = bookingService;
    }

    @GetMapping("/showtimes/{id}/seats")
    public String seats(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("seatMap", showtimeService.seatMap(id));
        BookingCart cart = (BookingCart) session.getAttribute(CART_ATTR);
        List<String> selected = (cart != null && cart.getShowtimeId().equals(id))
                ? cart.getSeats() : Collections.emptyList();
        model.addAttribute("selected", selected);
        model.addAttribute("maxSeats", BookingService.MAX_SEATS_PER_BOOKING);
        return "seats";
    }

    @PostMapping("/showtimes/{id}/seats")
    public String selectSeats(@PathVariable Long id,
                              @RequestParam(name = "seats", required = false) List<String> seats,
                              HttpSession session, RedirectAttributes redirect) {
        if (seats == null || seats.isEmpty()) {
            redirect.addFlashAttribute("error", "Vui lòng chọn ít nhất một ghế");
            return "redirect:/showtimes/" + id + "/seats";
        }
        if (seats.size() > BookingService.MAX_SEATS_PER_BOOKING) {
            redirect.addFlashAttribute("error", "Mỗi lần đặt tối đa " + BookingService.MAX_SEATS_PER_BOOKING + " ghế");
            return "redirect:/showtimes/" + id + "/seats";
        }
        session.setAttribute(CART_ATTR, new BookingCart(id, seats));
        return "redirect:/booking/confirm";
    }

    @GetMapping("/booking/confirm")
    public String confirm(HttpSession session, Model model) {
        BookingCart cart = (BookingCart) session.getAttribute(CART_ATTR);
        if (cart == null) {
            return "redirect:/";
        }
        Showtime showtime = showtimeService.get(cart.getShowtimeId());
        model.addAttribute("showtime", showtime);
        model.addAttribute("seats", cart.getSeats());
        model.addAttribute("total", showtime.getPrice() * cart.getSeats().size());
        return "confirm";
    }

    @PostMapping("/booking/confirm")
    public String doConfirm(HttpSession session, Principal principal, RedirectAttributes redirect) {
        BookingCart cart = (BookingCart) session.getAttribute(CART_ATTR);
        if (cart == null) {
            return "redirect:/";
        }
        try {
            bookingService.book(principal.getName(), cart.getShowtimeId(), cart.getSeats());
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            session.removeAttribute(CART_ATTR);
            return "redirect:/showtimes/" + cart.getShowtimeId() + "/seats";
        }
        session.removeAttribute(CART_ATTR);
        redirect.addFlashAttribute("booked", true);
        return "redirect:/bookings";
    }

    @PostMapping("/booking/cancel-cart")
    public String cancelCart(HttpSession session) {
        session.removeAttribute(CART_ATTR);
        return "redirect:/";
    }

    @GetMapping("/bookings")
    public String myBookings(Principal principal, Model model) {
        model.addAttribute("bookings", bookingService.findByUser(principal.getName()));
        return "my-bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            bookingService.cancel(id);
            redirect.addFlashAttribute("cancelled", true);
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings";
    }
}
