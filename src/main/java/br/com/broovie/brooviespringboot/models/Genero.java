package br.com.broovie.brooviespringboot.models;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "descricao_uq", columnNames = "descricao"))
public class Genero extends DefaultModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genero_seq")
    @SequenceGenerator(name = "genero_seq", initialValue = 10771, allocationSize = 1)
    private Long code;

    @Column(nullable = false)
    private String descricao;

}
