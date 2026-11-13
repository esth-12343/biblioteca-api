package com.ejemplo.biblioteca.controller;

import com.ejemplo.biblioteca.model.Libro;
import com.ejemplo.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST: recibe las peticiones HTTP y responde en JSON.
 * Ruta base: /api/libros
 */
@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService servicio;

    // Inyección de dependencias por constructor: Spring nos pasa el servicio
    public LibroController(LibroService servicio) {
        this.servicio = servicio;
    }

    /** GET /api/libros  -> lista todos los libros (200 OK) */
    @GetMapping
    public List<Libro> listar() {
        return servicio.listar();
    }

    /** GET /api/libros/{id}  -> un libro (200) o 404 si no existe */
    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtener(@PathVariable Long id) {
        return servicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/libros  -> crea un libro a partir del JSON del body (201 Created) */
    @PostMapping
    public ResponseEntity<Libro> crear(@Valid @RequestBody Libro libro) {
        Libro creado = servicio.guardar(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /** Si el JSON no cumple las validaciones, responde 400 con los mensajes de error */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }
}
