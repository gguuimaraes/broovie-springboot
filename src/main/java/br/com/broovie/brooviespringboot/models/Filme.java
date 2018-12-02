package br.com.broovie.brooviespringboot.models;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

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

    @Column(nullable = false)
    private String nome;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "filme_genero",
            foreignKey = @ForeignKey(name= "filme_fk"),
            joinColumns = @JoinColumn(name = "filme_code", referencedColumnName = "code"),
            inverseForeignKey =  @ForeignKey(name= "genero_fk"),
            inverseJoinColumns = @JoinColumn(name = "genero_code", referencedColumnName = "code"))
    private Set<Genero> generos;

    @Column(columnDefinition = "text")
    private String sinopse;

    private boolean adulto;

    private String fotoCapa;

    private int classificacaoIndicativa;
}