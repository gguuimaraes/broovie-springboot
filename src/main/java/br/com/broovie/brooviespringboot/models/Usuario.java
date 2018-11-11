package br.com.broovie.brooviespringboot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString


@Entity
@NamedQueries({
        @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u WHERE u.excluido = false"),
        @NamedQuery(name = "Usuario.pesquisar", query = "SELECT u FROM Usuario u WHERE UPPER(u.nome) LIKE CONCAT('%',UPPER(?1),'%') AND UPPER(u.nomeUsuario) LIKE CONCAT('%',UPPER(?2),'%') AND u.excluido = false"),
        @NamedQuery(name = "Usuario.autenticar", query = "SELECT u FROM Usuario u WHERE u.nomeUsuario = ?1 AND u.senha = ?2 AND u.excluido = false"),
        @NamedQuery(name = "Usuario.amigos", query = "SELECT u.amigos FROM Usuario u WHERE u.code = ?1 AND u.excluido = false")
})
@Table

public class Usuario extends DefaultModel {
    @Column(length = 70, nullable = false)
    private String nome;

    @Column(length = 30, unique = true, nullable = false)
    private String nomeUsuario;

    @Column(length = 40)
    private String email;

    @Column
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;
    @Column
    private String pais;
    @Column(nullable = false)
    private String senha;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY) 
    private List<Usuario> amigos = new ArrayList<>();

    @ManyToMany
    private List<Genero> generos = new ArrayList<>();
}
