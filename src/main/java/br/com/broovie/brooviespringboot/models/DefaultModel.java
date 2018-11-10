package br.com.broovie.brooviespringboot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.*;
import java.util.Date;

@Data

@MappedSuperclass
public abstract class DefaultModel extends ResourceSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long code;

    @JsonIgnore
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean excluido = false;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAtualizado = null;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataExcluido = null;

    @PrePersist
    protected void onCreate() {
        dataCadastro = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizado = new Date();
        if (excluido) dataExcluido = new Date();
    }
}
