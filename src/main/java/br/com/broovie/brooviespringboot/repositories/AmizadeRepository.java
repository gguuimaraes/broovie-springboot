package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Amizade;
import br.com.broovie.brooviespringboot.models.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmizadeRepository extends CrudRepository<Amizade, Long> {
    @Query(value = "SELECT a.solicitante FROM Amizade a WHERE a.situacao = 1 AND a.solicitado.code = ?1")
    List<Usuario> amigos(Long code);
    @Query(value = "SELECT a.solicitado FROM Amizade a WHERE a.situacao = 1 AND a.solicitante.code = ?1")
    List<Usuario> amigos2(Long code);

    @Query(value = "SELECT a FROM Amizade a WHERE a.solicitado.code = ?1 AND a.solicitante = ?2")
    Optional<Amizade> amizade(Long solicitado, Long solicitante);

    @Query(value = "SELECT a.solicitante FROM Amizade a WHERE a.solicitado.code = ?1 AND a.situacao = 0")
    List<Usuario> solicitacoesPendentes(long code);
}
