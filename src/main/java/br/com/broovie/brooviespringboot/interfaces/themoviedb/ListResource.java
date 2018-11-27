package br.com.broovie.brooviespringboot.interfaces.themoviedb;

import br.com.broovie.brooviespringboot.models.themoviedb.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ListResource {
    @GET("list/{codigo}")
    Call<List> get(@Path("codigo") int codigo, @Query("api_key") String api_key, @Query("language") String language);
}
