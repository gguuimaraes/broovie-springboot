package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Genero;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneroRepository extends CrudRepository<Genero, Long> {
    @Query(value = "SELECT g FROM Genero g WHERE g.code = ?1 AND g.excluido = false")
    Optional<Genero> findById(Long code);

    @Override
    @Query(value = "SELECT g FROM Genero g WHERE g.excluido = false")
    List<Genero> findAll();

    @Query(value = "SELECT g FROM Genero g WHERE UPPER(g.descricao) LIKE CONCAT('%',UPPER(?1),'%') AND g.excluido = false")
    List<Genero> pesquisar(String descricao);
}
