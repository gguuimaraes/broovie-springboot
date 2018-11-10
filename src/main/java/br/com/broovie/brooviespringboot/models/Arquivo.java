package br.com.broovie.brooviespringboot.models;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@NamedQueries({
        @NamedQuery(name = "Arquivo.findAll", query = "SELECT a FROM Arquivo a WHERE a.excluido = false"),
})
@Entity
@Table
public class Arquivo extends DefaultModel {
    @Lob
    private byte[] bytes;
}
