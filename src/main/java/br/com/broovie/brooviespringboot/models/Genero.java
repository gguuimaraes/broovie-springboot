package br.com.broovie.brooviespringboot.models;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table
public class Genero extends DefaultModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genero_seq")
    @SequenceGenerator(name = "genero_seq", initialValue = 10771, allocationSize = 1)
    private Long code;

    @Column
    private String descricao;

}
