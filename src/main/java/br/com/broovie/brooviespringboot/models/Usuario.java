package br.com.broovie.brooviespringboot.models;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "nome_usuario_uq", columnNames = "nomeUsuario"))
public class Usuario extends DefaultModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", initialValue = 372, allocationSize = 1)
    private Long code;

    @Column(length = 70, nullable = false)
    private String nome;

    @Column(length = 30, nullable = false)
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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_genero",
            foreignKey = @ForeignKey(name = "usuario_fk"),
            joinColumns = @JoinColumn(name = "usuario_code", referencedColumnName = "code"),
            inverseForeignKey = @ForeignKey(name = "genero_fk"),
            inverseJoinColumns = @JoinColumn(name = "genero_code", referencedColumnName = "code"))
    private Set<Genero> generos = new HashSet<>();

}
