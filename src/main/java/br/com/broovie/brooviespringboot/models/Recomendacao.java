package br.com.broovie.brooviespringboot.models;

import lombok.*;

import org.springframework.hateoas.ResourceSupport;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name="recomendacao_unique", columnNames = {"usuario_code", "filme_code", "tipoRecomendacao"})
})
public class Recomendacao extends ResourceSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recomendacao_seq")
    @SequenceGenerator(name = "recomendacao_seq", initialValue = 1, allocationSize = 1)
    private Long code;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Filme filme;

    @Column(nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private Avaliacao.Nota notaCalculada;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRecomendacao;

    @Enumerated(value = EnumType.STRING)
    private TipoRecomendacao tipoRecomendacao;

    @PrePersist
    protected void onCreate() {
        dataRecomendacao = new Date();
    }


    public enum TipoRecomendacao {
        USER_SIMILARITY,
        MATRIX_FACTORIZATION;
    }
}
