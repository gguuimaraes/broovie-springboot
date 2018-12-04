package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Avaliacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends CrudRepository<Avaliacao, Long> {
    @Query(value = "SELECT a FROM Avaliacao a WHERE a.code = ?1 AND a.excluido = false")
    Optional<Avaliacao> findById(Long code);

    @Override
    @Query(value = "SELECT a FROM Avaliacao a WHERE a.excluido = false")
    List<Avaliacao> findAll();

    @Query(value = "SELECT a FROM Avaliacao a WHERE a.usuario.id = ?1")
    List<Avaliacao> avaliacaoPorUsuario(Long code, Pageable pageable);
}
