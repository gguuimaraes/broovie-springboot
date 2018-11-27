package br.com.broovie.brooviespringboot.repositories;

import br.com.broovie.brooviespringboot.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query(value = "SELECT u FROM Usuario u WHERE u.code = ?1 AND u.excluido = false")
    Optional<Usuario> findById(Long code);

    @Override
    @Query(value = "SELECT u FROM Usuario u WHERE u.excluido = false")
    List<Usuario> findAll();

    @Query(value = "SELECT u FROM Usuario u WHERE u.excluido = false AND (:nome IS NULL OR UPPER(u.nome) LIKE CONCAT('%',UPPER(:nome),'%')) AND (:nomeUsuario IS NULL OR UPPER(u.nomeUsuario) LIKE CONCAT('%',UPPER(:nomeUsuario),'%'))")
    List<Usuario> pesquisar(@Param("nome") String nome, @Param("nomeUsuario") String nomeUsuario);

    @Query(value = "SELECT u FROM Usuario u WHERE u.nomeUsuario = ?1 AND u.senha = ?2 AND u.excluido = false")
    Usuario autenticar(String nomeUsuario, String senha);

    @Query(value = "SELECT u.code FROM Usuario u WHERE UPPER(u.nome) LIKE CONCAT('%',UPPER(?1),'%') AND u.excluido = false")
    List<Long> codigos(String nome);

    Optional<Usuario> findByNomeUsuario(String nomeUsuario);

    int countAllByExcluidoIsFalse();
}
