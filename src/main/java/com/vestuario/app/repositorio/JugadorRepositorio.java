package com.vestuario.app.repositorio;
 
import com.vestuario.app.modelo.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
/**
 * Los jugadores se guardan y eliminan en cascada desde Club.
 * Este repositorio se usa para consultas globales, como el total de jugadores.
 */
@Repository
public interface JugadorRepositorio extends JpaRepository<Jugador, Long> {
}
