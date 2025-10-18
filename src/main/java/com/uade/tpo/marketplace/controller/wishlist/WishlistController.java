package com.uade.tpo.marketplace.controller.wishlist;

import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.Wishlist;
import com.uade.tpo.marketplace.repository.UserRepository;
import com.uade.tpo.marketplace.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserRepository userRepository;

    // --- NUEVO MÉTODO ---
    // Obtiene la wishlist del usuario actualmente autenticado
    @GetMapping("/me")
    public ResponseEntity<Wishlist> getCurrentUserWishlist(Authentication auth) {
        // Obtenemos el email del token
        String email = auth.getName();
        // Buscamos al usuario en la base de datos
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado con email: " + email));
        
        // Obtenemos la wishlist a través del usuario y la devolvemos
        Wishlist wishlist = wishlistService.getWishlistById(user.getWishlist().getId());
        return ResponseEntity.ok(wishlist);
    }

    // --- NUEVO MÉTODO ---
    // Agrega un juego a la wishlist del usuario autenticado
    @PutMapping("/me/add")
    public ResponseEntity<Wishlist> addGameToCurrentUserWishlist(Authentication auth, @RequestBody WishlistRequest request) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado con email: " + email));

        // Usamos el servicio existente para agregar el juego a la wishlist del usuario
        Wishlist updatedWishlist = wishlistService.addGame(user.getWishlist().getId(), request.getGameId());
        
        if (updatedWishlist != null) {
            return ResponseEntity.ok(updatedWishlist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- MÉTODOS ANTIGUOS (los mantenemos si son necesarios para otra cosa) ---
    @GetMapping("/{id}")
    public ResponseEntity<Wishlist> getWishlistById(@PathVariable Long id) {
        Wishlist wishlist = wishlistService.getWishlistById(id);
        return ResponseEntity.ok(wishlist);
    }

    @PutMapping("/{id}/add")
    public ResponseEntity<Wishlist> addGame(@PathVariable Long id, @RequestBody WishlistRequest wishlistRequest) {
        Wishlist updatedWishlist = wishlistService.addGame(id, wishlistRequest.getGameId());
        if (updatedWishlist != null) {
            return ResponseEntity.ok(updatedWishlist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<Wishlist> deleteGame(@PathVariable Long id, @RequestBody WishlistRequest wishlistRequest) {
        Wishlist updatedWishlist = wishlistService.deleteGame(id, wishlistRequest.getGameId());
        if (updatedWishlist != null) {
            return ResponseEntity.ok(updatedWishlist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
