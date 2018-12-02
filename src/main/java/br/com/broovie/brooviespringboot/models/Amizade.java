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
@Table(uniqueConstraints = @UniqueConstraint(name = "solicitante_solicitado_uq", columnNames = {"solicitante_code", "solicitado_code"}))
public class Amizade extends ResourceSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "amizade_seq")
    @SequenceGenerator(name = "amizade_seq", initialValue = 1, allocationSize = 1)
    private Long code;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSolicitacao;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "solicitante_fk"))
    private Usuario solicitante;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "solicitado_fk"))
    private Usuario solicitado;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataConfirmacao;

    @Column(nullable = false)
    @Enumerated
    private Situacao situacao = Situacao.PENDENTE;

    public enum Situacao {
        PENDENTE,
        APROVADA,
        REJEITADA;
    }
}
