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
public class Avaliacao extends DefaultModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "avaliacao_seq")
    @SequenceGenerator(name = "avaliacao_seq", initialValue = 371, allocationSize = 1)
    private Long code;

    @ManyToOne
    private Usuario usuario;

    @OneToOne
    private Filme filme;

    @Enumerated(EnumType.ORDINAL)
    private Nota nota;

    public enum Nota {
        INUTIL,
        PESSIMO,
        RUIM,
        MEDIO,
        BOM,
        OTIMO
    }
}
