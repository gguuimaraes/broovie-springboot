package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    @Query(value = "SELECT u FROM Usuario u WHERE u.code = ?1 AND u.excluido = false")
    Optional<Usuario> findById(Long code);

    @Override
    @Query(name = "Usuario.findAll")
    List<Usuario> findAll();

    List<Usuario> pesquisar(String nome, String nomeUsuario);

    Usuario autenticar(String nomeUsuario, String senha);

    List<Usuario> amigos(Long code);

    @Query(value = "SELECT COUNT(u) FROM Usuario u")
    int quantidade();
}
