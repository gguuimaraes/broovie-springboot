package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Arquivo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArquivoRepository extends CrudRepository<Arquivo, Long> {
    @Query(value = "SELECT a FROM Arquivo a WHERE a.code = ?1 AND a.excluido = false")
    Optional<Arquivo> findById(Long code);

    @Override
    @Query(name = "Arquivo.findAll")
    List<Arquivo> findAll();
}
