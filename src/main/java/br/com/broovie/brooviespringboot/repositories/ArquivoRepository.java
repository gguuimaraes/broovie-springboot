package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Arquivo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArquivoRepository extends CrudRepository<Arquivo, Long> {
    @Override
    @Query(name = "Arquivo.findAll")
    List<Arquivo> findAll();
}
