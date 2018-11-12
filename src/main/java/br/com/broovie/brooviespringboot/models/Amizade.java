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
@Table
public class Amizade extends ResourceSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long code;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSolicitacao;

    @OneToOne(optional = false)
    private Usuario solicitante;

    @OneToOne(optional = false)
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
