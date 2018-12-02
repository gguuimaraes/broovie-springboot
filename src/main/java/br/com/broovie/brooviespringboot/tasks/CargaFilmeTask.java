package br.com.broovie.brooviespringboot.tasks;

import br.com.broovie.brooviespringboot.interfaces.themoviedb.ListResource;
import br.com.broovie.brooviespringboot.models.Filme;
import br.com.broovie.brooviespringboot.models.themoviedb.List;
import br.com.broovie.brooviespringboot.repositories.FilmeRepository;
import br.com.broovie.brooviespringboot.repositories.GeneroRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Component
public class CargaFilmeTask {
    @Autowired
    FilmeRepository filmeRepository;

    @Autowired
    GeneroRepository generoRepository;

    private static final String ENDPOINT = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "0c68ad7dd2c1a9d95335a196b3163384";
    private static final String LANGUAGE = "pt-BR";
    private static int listaContador = 937;
    private Gson gson = null;
    private Retrofit retrofit = null;
    private ListResource listResource = null;
    private Set<Filme> filmesGeral = new HashSet<>();


    //@Scheduled(initialDelay = 1000, fixedRate = 1)
    public void cargaDeFilme() {
        try {
            if (filmesGeral.isEmpty()) filmesGeral.addAll(filmeRepository.findAll());
            if (gson == null)
                gson = new GsonBuilder().setLenient().create();
            if (retrofit == null)
                retrofit = new Retrofit.Builder().baseUrl(ENDPOINT).addConverterFactory(GsonConverterFactory.create(gson)).build();
            if (listResource == null)
                listResource = retrofit.create(ListResource.class);

            Response<List> resposta = listResource.get(listaContador++, API_KEY, LANGUAGE).execute();


            if (resposta.isSuccessful()) {
                Set<Filme> filmes = new HashSet<>();
                resposta.body().getItems().forEach(item -> {
                    if (saveImage(item.getPoster_path(), item.getId().toString())) {
                        Filme filme = Filme.builder()
                                .code(item.getId())
                                .nome(item.getTitle())
                                .sinopse(item.getOverview())
                                .adulto(item.isAdult())
                                .fotoCapa(String.format("/images/%d.jpg", item.getId()))
                                .generos(new HashSet<>()).build();
                        for (long gen_id : item.getGenre_ids()) {
                            filme.getGeneros().add(generoRepository.findById(gen_id).get());
                        }
                        if (filmesGeral.add(filme)) {
                            filmes.add(filme);
                            System.out.printf("Adicionando %s na lista, código %d%n", filme.getNome(), filme.getCode());
                        }
                    }
                });
                System.out.printf("Lista %d, +%d filmes, total %d%n", listaContador - 1, filmes.size(), filmesGeral.size());
                filmeRepository.saveAll(filmes);
            } else System.out.println("Falha ao obter lista de código " + (listaContador - 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean saveImage(String url, String nome) {
        try (InputStream in = new URL("http://image.tmdb.org/t/p/original" + url).openStream()) {
            Files.copy(in, Paths.get("C:\\broovie\\images\\" + nome + ".jpg"));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
