package br.com.broovie.brooviespringboot.models;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@NamedQueries({
        @NamedQuery(name = "Genero.findAll", query = "SELECT g FROM Genero g WHERE g.excluido = false"),
        @NamedQuery(name = "Genero.pesquisar", query = "SELECT g FROM Genero g WHERE UPPER(g.descricao) LIKE CONCAT('%',UPPER(?1),'%') AND g.excluido = false")
})
@Entity
@Table
public class Genero extends DefaultModel {
    @Column
    private String descricao;

}
