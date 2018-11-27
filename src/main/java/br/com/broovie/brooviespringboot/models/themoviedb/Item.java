package br.com.broovie.brooviespringboot.models.themoviedb;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Item {
    private float vote_average;
    private int vote_count;
    private Long id;
    private boolean video;
    private String media_type;
    private String title;
    private float popularity;
    private String poster_path;
    private String original_language;
    private String original_title;
    private List<Integer> genre_ids;
    private String backdrop_path;
    private boolean adult;
    private String overview;
    private Date release_date;
}
