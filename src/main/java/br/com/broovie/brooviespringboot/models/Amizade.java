package br.com.broovie.brooviespringboot.models;

import org.springframework.hateoas.ResourceSupport;

import javax.persistence.*;
import java.util.Date;

public class Amizade extends ResourceSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long code;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSolicitacao;

    @Column(nullable = false)
    @OneToOne
    private Usuario solicitante;

    @Column(nullable = false)
    @OneToOne
    private Usuario solicitado;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataConfirmacao;

    @Column(nullable = false)
    private Situacao situacao  = Situacao.PENDENTE;

    public enum Situacao {
        PENDENTE,
        APROVADA,
        REJEITADA;
    }
}
