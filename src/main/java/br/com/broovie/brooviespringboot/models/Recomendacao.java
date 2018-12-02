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
@Table(uniqueConstraints = @UniqueConstraint(name = "usuario_filme_tipo_recomendacao_uq", columnNames = {"usuario_code", "filme_code", "tipoRecomendacao"}))
public class Recomendacao extends ResourceSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recomendacao_seq")
    @SequenceGenerator(name = "recomendacao_seq", initialValue = 1, allocationSize = 1)
    private Long code;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "usuario_fk"))
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "filme_fk"))
    private Filme filme;

    @Column(nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private Avaliacao.Nota notaCalculada;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRecomendacao;

    @Column(nullable = false)
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
