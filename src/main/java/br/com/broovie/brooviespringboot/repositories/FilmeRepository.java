package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Arquivo;
import br.com.broovie.brooviespringboot.models.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {
    @Query(value = "SELECT f FROM Filme f WHERE f.code = ?1 AND f.excluido = false")
    Optional<Filme> findById(Long code);

    @Override
    @Query(name = "Filme.findAll")
    List<Filme> findAll();

    @Transactional
    List<Filme> pesquisar(String nome);

    @Transactional
    @Query(name = "Filme.fotoCapa")
    Arquivo fotoCapa(Long id);
}
