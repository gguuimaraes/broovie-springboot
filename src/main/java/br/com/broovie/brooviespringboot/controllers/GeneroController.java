package br.com.broovie.brooviespringboot.controllers;

import br.com.broovie.brooviespringboot.exceptions.ResourceNotFoundException;
import br.com.broovie.brooviespringboot.interfaces.GenericOperations;
import br.com.broovie.brooviespringboot.models.Genero;
import br.com.broovie.brooviespringboot.repositories.GeneroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping
public class GeneroController implements GenericOperations<Genero> {
    @Autowired
    GeneroRepository generoRepository;

    @Override
    @PostMapping(path = "/genero", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Genero> create(@RequestBody Genero o) {
        generoRepository.save(o);
        o.add(linkTo(methodOn(GeneroController.class).read(o.getCode())).withSelfRel());
        return new ResponseEntity<>(o, o.getCode() != null ? HttpStatus.CREATED : HttpStatus.NO_CONTENT);
    }

    @Override
    @PutMapping(path = "/genero", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Genero> update(@RequestBody Genero o) {
        Optional<Genero> result = generoRepository.findById(o.getCode());
        result.ifPresent(genero -> {
            genero.setDescricao(o.getDescricao());
            generoRepository.save(genero);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Genero.class, o.getCode()));
        o.add(linkTo(methodOn(GeneroController.class).read(o.getCode())).withSelfRel());
        return new ResponseEntity<>(o, HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "/genero/{code}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Genero> read(@PathVariable(value = "code") long code) {
        Optional<Genero> result = generoRepository.findById(code);
        if (result.isPresent()) {
            Genero entity = result.get();
            entity.add(linkTo(methodOn(GeneroController.class).read(entity.getCode())).withSelfRel());
            entity.add(linkTo(methodOn(GeneroController.class).read()).withRel("all"));
            return new ResponseEntity<>(entity, HttpStatus.OK);
        }
        throw new ResourceNotFoundException(Genero.class, code);
    }

    @Override
    @DeleteMapping(path = "/genero/{code}")
    public HttpEntity<Genero> delete(@PathVariable(value = "code") long code) {
        Optional<Genero> result = generoRepository.findById(code);
        result.ifPresent(genero -> {
            genero.setExcluido(true);
            generoRepository.save(genero);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Genero.class, code));
        result.get().add(linkTo(methodOn(GeneroController.class).read(code)).withSelfRel());
        return new ResponseEntity<>(result.get(), HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "/generos", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Genero>> read() {
        List<Genero> generos = generoRepository.findAll();
        generos.forEach(c -> {
            c.add(linkTo(methodOn(GeneroController.class).read(c.getCode())).withSelfRel());
        });
        return new ResponseEntity<>(generos, HttpStatus.OK);
    }

    @GetMapping(path = "/genero/pesquisar", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Genero>> pesquisar(@RequestParam(value = "nome") String nome) {
        List<Genero> generos = generoRepository.pesquisar(nome);
        generos.forEach(c -> {
            c.add(linkTo(methodOn(GeneroController.class).read(c.getCode())).withSelfRel());
        });
        return new ResponseEntity<>(generos, HttpStatus.OK);
    }
}

