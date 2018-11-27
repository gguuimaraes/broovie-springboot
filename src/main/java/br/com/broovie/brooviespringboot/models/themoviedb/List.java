package br.com.broovie.brooviespringboot.models.themoviedb;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class List {
    private String created_by;
    private String description;
    private int favorite_count;
    private int id;
    private java.util.List<Item> items;
    private int item_count;
    private String iso_639_1;
    private String name;
    private String poster_path;

}
