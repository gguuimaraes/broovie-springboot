package br.com.broovie.brooviespringboot.models;


import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table
public class Filme extends DefaultModel {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO, generator = "filme_seq")
    //@SequenceGenerator(name = "filme_seq", initialValue = 99, allocationSize = 1)
    private Long code;

    @Column
    private String nome;

    @ManyToMany
    private List<Genero> generos;

    @Column(columnDefinition = "text")
    private String sinopse;

    private boolean adulto;

    private String fotoCapa;

    private int classificacaoIndicativa;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Filme filme = (Filme) o;
        return Objects.equals(code, filme.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), code);
    }
}