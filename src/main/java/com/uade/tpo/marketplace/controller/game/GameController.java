package com.uade.tpo.marketplace.controller.game;

import com.uade.tpo.marketplace.entity.Game;
import com.uade.tpo.marketplace.exceptions.InvalidDiscountException;
import com.uade.tpo.marketplace.exceptions.NegativePriceException;
import com.uade.tpo.marketplace.exceptions.NegativeStockException;
import com.uade.tpo.marketplace.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("games")
public class GameController {

    @Autowired
    private GameService gameService;

    // --- ENDPOINTS PÚBLICOS ---
    @GetMapping("/get/available")
    public ResponseEntity<List<Game>> getAllAvailableGames() {
        return ResponseEntity.ok(gameService.getAllAvailableGames());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        // Asumiendo que tienes un método en tu servicio para buscar por ID
        Game game = gameService.getGameById(id); // Necesitarás crear este método
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.notFound().build();
    }
    
    // ... (tus otros endpoints GET para buscar por categoría, precio, etc. van aquí)

    // --- ENDPOINTS DE ADMINISTRADOR ---

    @GetMapping("/admin")
    public ResponseEntity<List<Game>> getAllGamesForAdmin() {
        return ResponseEntity.ok(gameService.getAllGames());
    }
    
    @GetMapping("/admin/{id}")
    public ResponseEntity<Game> getGameByIdForAdmin(@PathVariable Long id) {
        Game game = gameService.getGameById(id); // Reutilizamos el método
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/admin/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createGameWithImage(@RequestPart("gameData") GameRequest gameRequest, @RequestPart("imagen") MultipartFile imagen) {
        try {
            // Guardar imagen y obtener URL
            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, imagen.getBytes());
            String imagenUrl = "http://localhost:8080/uploads/" + fileName; // O tu URL base
            gameRequest.setImageUrl(imagenUrl);

            Game result = gameService.createGame(gameRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear el videojuego: " + e.getMessage());
        }
    }

    @PutMapping(value = "/admin/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateGameWithImage(
            @PathVariable Long id,
            @RequestPart("gameData") GameRequest gameRequest,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        try {
            // Si se sube una imagen nueva, la guardamos y actualizamos la URL
            if (imagen != null && !imagen.isEmpty()) {
                String uploadDir = "uploads/";
                Files.createDirectories(Paths.get(uploadDir));
                String fileName = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                Files.write(filePath, imagen.getBytes());
                String imagenUrl = "http://localhost:8080/uploads/" + fileName;
                gameRequest.setImageUrl(imagenUrl);
            }

            Game updatedGame = gameService.editGame(id, gameRequest);

            if (updatedGame != null) {
                return ResponseEntity.ok(updatedGame);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al editar el videojuego: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
