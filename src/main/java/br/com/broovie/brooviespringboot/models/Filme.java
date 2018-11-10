package br.com.broovie.brooviespringboot.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@NamedQueries({
        @NamedQuery(name = "Filme.findAll", query = "SELECT f FROM Filme f WHERE f.excluido = false"),
        @NamedQuery(name = "Filme.pesquisar", query = "SELECT f FROM Filme f WHERE UPPER(f.nome) LIKE CONCAT('%',UPPER(?1),'%') AND f.excluido = false"),
        @NamedQuery(name = "Filme.fotoCapa", query = "SELECT a FROM Arquivo a, Filme f WHERE f.code = ?1 AND a = f.fotoCapa AND f.excluido = false AND a.excluido = false")
})
@Entity
@Table
public class Filme extends DefaultModel {
    @Column(unique = true)
    private String nome;

    @ManyToMany
    private List<Genero> generos;

    @Column(columnDefinition = "text")
    private String sinopse;

    private boolean adulto;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private Arquivo fotoCapa;

    private int classificacaoIndicativa;
}