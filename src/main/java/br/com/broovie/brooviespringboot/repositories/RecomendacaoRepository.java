package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Recomendacao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RecomendacaoRepository extends CrudRepository<Recomendacao, Long> {
    @Query(value = "SELECT r FROM Recomendacao r WHERE r.usuario.code = ?1 AND r.tipoRecomendacao = ?2")
    Set<Recomendacao> recomendacoesPorUsuarioTipo(long codigoUsuario, Recomendacao.TipoRecomendacao tipo);
}
