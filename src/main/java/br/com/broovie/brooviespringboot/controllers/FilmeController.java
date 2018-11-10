package br.com.broovie.brooviespringboot.controllers;

import br.com.broovie.brooviespringboot.exceptions.ResourceNotFoundException;
import br.com.broovie.brooviespringboot.interfaces.GenericOperations;
import br.com.broovie.brooviespringboot.models.Arquivo;
import br.com.broovie.brooviespringboot.models.Filme;
import br.com.broovie.brooviespringboot.repositories.AvaliacaoRepository;
import br.com.broovie.brooviespringboot.repositories.FilmeRepository;
import br.com.broovie.brooviespringboot.repositories.UsuarioRepository;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping
public class FilmeController implements GenericOperations<Filme> {
    @Autowired
    FilmeRepository filmeRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    AvaliacaoRepository avaliacaoRepository;

    @Override
    @PostMapping(path = "/filme", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Filme> create(@RequestBody Filme o) {
        filmeRepository.save(o);
        o.add(linkTo(methodOn(FilmeController.class).read(o.getCode())).withSelfRel());
        return new ResponseEntity<>(o, o.getCode() != null ? HttpStatus.CREATED : HttpStatus.NO_CONTENT);
    }

    @Override
    @PutMapping(path = "/filme", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Filme> update(@RequestBody Filme o) {
        Optional<Filme> result = filmeRepository.findById(o.getCode());
        result.ifPresent(filme -> {
            filme.setGeneros(o.getGeneros());
            filme.setNome(o.getNome());
            filme.setSinopse(o.getSinopse());
            filme.setAdulto(o.isAdulto());
            filme.setFotoCapa(o.getFotoCapa());
            filme.setClassificacaoIndicativa(o.getClassificacaoIndicativa());
            filmeRepository.save(filme);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Filme.class, o.getCode()));
        o.add(linkTo(methodOn(FilmeController.class).read(o.getCode())).withSelfRel());
        return new ResponseEntity<>(o, HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "/filme/{code}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Filme> read(@PathVariable(value = "code") long code) {
        Optional<Filme> result = filmeRepository.findById(code);
        if (result.isPresent()) {
            Filme entity = result.get();
            entity.add(linkTo(methodOn(FilmeController.class).read(entity.getCode())).withSelfRel());
            entity.add(linkTo(methodOn(FilmeController.class).read()).withRel("all"));
            return new ResponseEntity<>(entity, HttpStatus.OK);
        }
        throw new ResourceNotFoundException(Filme.class, code);
    }

    @Override
    @DeleteMapping(path = "/filme/{code}")
    public HttpEntity<Filme> delete(@PathVariable(value = "code") long code) {
        Optional<Filme> result = filmeRepository.findById(code);
        result.ifPresent(filme -> {
            filme.setExcluido(true);
            filmeRepository.save(filme);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Filme.class, code));
        result.get().add(linkTo(methodOn(FilmeController.class).read(code)).withSelfRel());
        return new ResponseEntity<>(result.get(), HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "/filmes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Filme>> read() {
        List<Filme> filmes = filmeRepository.findAll();
        filmes.forEach(c -> {
            c.add(linkTo(methodOn(FilmeController.class).read(c.getCode())).withSelfRel());
        });
        return new ResponseEntity<>(filmes, HttpStatus.OK);
    }

    @GetMapping(path = "/filmes/pesquisar", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Filme>> pesquisar(@RequestParam(value = "nome") String nome) {
        List<Filme> filmes = filmeRepository.pesquisar(nome);
        filmes.forEach(f -> {
            f.add(linkTo(methodOn(FilmeController.class).read(f.getCode())).withSelfRel());
        });
        return new ResponseEntity<>(filmes, HttpStatus.OK);
    }

    @GetMapping(path = "/filme/{code}/fotocapa", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Arquivo> fotoCapa(@PathVariable(value = "code") long code) {
        Arquivo fotoCapa = filmeRepository.fotoCapa(code);
        if (fotoCapa != null) {
            fotoCapa.add(linkTo(methodOn(FilmeController.class).read(code)).withRel("filme"));
            fotoCapa.add(linkTo(methodOn(FilmeController.class).fotoCapa(code)).withSelfRel());
            return new ResponseEntity<>(fotoCapa, HttpStatus.OK);
        }
        throw new ResourceNotFoundException(Filme.class, code);
    }

    @GetMapping(path = "/filme/{code}/recomendados", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Filme>> recomendados(@PathVariable(value = "code") long code) {
        List<Filme> filmes = new ArrayList<>();
        try {
            PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
            pgSimpleDataSource.setDatabaseName("broovie-springboot");
            pgSimpleDataSource.setPortNumber(5432);
            pgSimpleDataSource.setUser("postgres");
            pgSimpleDataSource.setPassword("142697");
            DataModel model = new PostgreSQLJDBCDataModel(pgSimpleDataSource, "avaliacao", "usuario_code", "filme_code", "nota", null);
            RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
                public Recommender buildRecommender(DataModel model) throws TasteException {
                    UserSimilarity similarity = new LogLikelihoodSimilarity(model);

                    UserNeighborhood neighborhood = new NearestNUserNeighborhood(usuarioRepository.quantidade() / 5, similarity, model);
                    return new GenericUserBasedRecommender(model, neighborhood, similarity);
                }
            };

            Recommender recommender = recommenderBuilder.buildRecommender(model);
            List<RecommendedItem> recomendations = recommender.recommend(code, 10);
            for (RecommendedItem recommendedItem : recomendations) {
                filmeRepository.findById(recommendedItem.getItemID()).ifPresent(f -> {
                    f.add(linkTo(methodOn(FilmeController.class).read(f.getCode())).withSelfRel());
                    filmes.add(f);
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(filmes, HttpStatus.OK);
    }
}

