package com.ejemplo.biblioteca.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Modelo (objeto) que procesa la API. Tiene 6 campos (la práctica pide mínimo 4).
 *
 * Es un "record" de Java: genera automáticamente constructor, getters,
 * equals, hashCode y toString. Spring lo convierte a/desde JSON solo.
 */
public record Libro(
        Long id,                                              // lo asigna el servidor

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "El autor es obligatorio")
        String autor,

        @Min(value = 1450, message = "El año debe ser >= 1450")
        @Max(value = 2100, message = "El año debe ser <= 2100")
        int anioPublicacion,

        @NotBlank(message = "El género es obligatorio")
        String genero,

        @PositiveOrZero(message = "El precio no puede ser negativo")
        double precio
) {
    /** Devuelve una copia del libro con el id asignado. */
    public Libro conId(Long nuevoId) {
        return new Libro(nuevoId, titulo, autor, anioPublicacion, genero, precio);
    }
}
