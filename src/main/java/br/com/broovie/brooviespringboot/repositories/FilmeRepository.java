package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {
    @Query(value = "SELECT f FROM Filme f WHERE f.code = ?1 AND f.excluido = false")
    Optional<Filme> findById(Long code);

    @Override
    @Query(value = "SELECT f FROM Filme f WHERE f.excluido = false")
    List<Filme> findAll();

    @Query(value = "SELECT f FROM Filme f WHERE UPPER(f.nome) LIKE CONCAT('%',UPPER(?1),'%') AND f.excluido = false")
    List<Filme> pesquisar(String nome);

    @Query(value = "SELECT f FROM Filme f JOIN Avaliacao a ON a.filme.code = f.code WHERE a.nota = 5 GROUP BY f ORDER BY COUNT(a) DESC")
    List<Filme> emAlta();
}
