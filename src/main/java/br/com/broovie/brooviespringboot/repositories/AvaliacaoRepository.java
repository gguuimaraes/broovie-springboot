package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Avaliacao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends CrudRepository<Avaliacao, Long> {
    @Override
    @Query(name = "Avaliacao.findAll")
    List<Avaliacao> findAll();
}
