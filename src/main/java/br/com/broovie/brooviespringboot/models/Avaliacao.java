package br.com.broovie.brooviespringboot.models;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@NamedQueries({
        @NamedQuery(name = "Avaliacao.findAll", query = "SELECT a FROM Avaliacao a WHERE a.excluido = false"),
})
@Entity
@Table
public class Avaliacao extends DefaultModel {
    @OneToOne
    private Usuario usuario;
    @OneToOne
    private Filme filme;
    @Enumerated
    private Nota nota;

    public enum Nota {
        PESSIMO,
        RUIM,
        MEDIO,
        BOM,
        OTIMO
    }
}
