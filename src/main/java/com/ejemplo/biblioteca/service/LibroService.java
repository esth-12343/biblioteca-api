package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.model.Libro;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lógica de negocio. Guarda los libros en memoria (un Map),
 * así no necesitamos base de datos. Al reiniciar la app se pierden.
 */
@Service
public class LibroService {

    private final Map<Long, Libro> libros = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    public LibroService() {
        // Datos iniciales para que el GET muestre algo desde el inicio
        guardar(new Libro(null, "Cien años de soledad", "Gabriel García Márquez", 1967, "Novela", 59.90));
        guardar(new Libro(null, "La ciudad y los perros", "Mario Vargas Llosa", 1963, "Novela", 45.50));
    }

    public List<Libro> listar() {
        return new ArrayList<>(libros.values());
    }

    public Optional<Libro> buscarPorId(Long id) {
        return Optional.ofNullable(libros.get(id));
    }

    public Libro guardar(Libro libro) {
        Libro nuevo = libro.conId(secuencia.incrementAndGet());
        libros.put(nuevo.id(), nuevo);
        return nuevo;
    }
}
