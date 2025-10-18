package com.uade.tpo.marketplace.controller.order;

import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.NoStockAvailableException;
import com.uade.tpo.marketplace.repository.UserRepository;
import com.uade.tpo.marketplace.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*; // Asegúrate de tener GetMapping

import java.util.List; // Y List

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Object> createOrder(Authentication auth, @RequestBody OrderRequest orderRequest) throws NoStockAvailableException {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        OrderResponse result = orderService.createOrder(user, orderRequest);
        return ResponseEntity.ok(result);
    }

    // --- NUEVO MÉTODO ---
    // Obtiene el historial de órdenes del usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getCurrentUserOrders(Authentication auth) {
        String email = auth.getName();
        List<OrderResponse> userOrders = orderService.getOrdersByUserEmail(email);
        return ResponseEntity.ok(userOrders);
    }
}
