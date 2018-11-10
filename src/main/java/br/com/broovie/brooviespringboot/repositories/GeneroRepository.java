package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Genero;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneroRepository extends CrudRepository<Genero, Long> {
    @Override
    @Query(name = "Genero.findAll")
    List<Genero> findAll();

    List<Genero> pesquisar(String descricao);
}
