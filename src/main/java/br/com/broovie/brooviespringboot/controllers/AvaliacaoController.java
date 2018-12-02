package br.com.broovie.brooviespringboot.controllers;

import br.com.broovie.brooviespringboot.exceptions.ResourceNotFoundException;
import br.com.broovie.brooviespringboot.interfaces.GenericOperations;
import br.com.broovie.brooviespringboot.models.Avaliacao;
import br.com.broovie.brooviespringboot.repositories.AvaliacaoRepository;
import br.com.broovie.brooviespringboot.repositories.FilmeRepository;
import br.com.broovie.brooviespringboot.repositories.UsuarioRepository;
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
public class AvaliacaoController implements GenericOperations<Avaliacao> {
    @Autowired
    AvaliacaoRepository avaliacaoRepository;

    @Override
    @PostMapping(path = "avaliacao", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Avaliacao> create(@RequestBody Avaliacao o) {
        avaliacaoRepository.save(o);
        o.add(linkTo(methodOn(AvaliacaoController.class).read(o.getCode())).withSelfRel());
        return new ResponseEntity<>(o, o.getCode() != null ? HttpStatus.CREATED : HttpStatus.NO_CONTENT);
    }

    @Override
    @PutMapping(path = "avaliacao", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Avaliacao> update(@RequestBody Avaliacao o) {
        Optional<Avaliacao> result = avaliacaoRepository.findById(o.getCode());
        result.ifPresent(avaliacao -> {
            avaliacao.setFilme(o.getFilme());
            avaliacao.setNota(o.getNota());
            avaliacao.setUsuario(o.getUsuario());
            avaliacaoRepository.save(avaliacao);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Avaliacao.class, "code",o.getCode()));
        o.add(linkTo(methodOn(AvaliacaoController.class).read(o.getCode())).withSelfRel());
        return new ResponseEntity<>(o, HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "avaliacao/{code}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Avaliacao> read(@PathVariable(value = "code") long code) {
        Optional<Avaliacao> result = avaliacaoRepository.findById(code);
        if (result.isPresent()) {
            Avaliacao entity = result.get();
            entity.add(linkTo(methodOn(AvaliacaoController.class).read(entity.getCode())).withSelfRel());
            entity.add(linkTo(methodOn(AvaliacaoController.class).read()).withRel("all"));
            return new ResponseEntity<>(entity, HttpStatus.OK);
        }
        throw new ResourceNotFoundException(Avaliacao.class,"code", code);
    }

    @Override
    @DeleteMapping(path = "avaliacao/{code}")
    public HttpEntity<Avaliacao> delete(@PathVariable(value = "code") long code) {
        Optional<Avaliacao> result = avaliacaoRepository.findById(code);
        result.ifPresent(avaliacao -> {
            avaliacao.setExcluido(true);
            avaliacaoRepository.save(avaliacao);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Avaliacao.class, "code",code));
        result.get().add(linkTo(methodOn(AvaliacaoController.class).read(code)).withSelfRel());
        return new ResponseEntity<>(result.get(), HttpStatus.CREATED);
    }

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    FilmeRepository filmeRepository;

    @Override
    @GetMapping(path = "avaliacoes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Avaliacao>> read() {
        List<Avaliacao> avaliacaos = avaliacaoRepository.findAll();
        avaliacaos.forEach(c -> {
            c.add(linkTo(methodOn(AvaliacaoController.class).read(c.getCode())).withSelfRel());
        });
        return new ResponseEntity<>(avaliacaos, HttpStatus.OK);
    }

    @GetMapping(path = "usuario/{codigoUsuario}/avaliacoes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Avaliacao>> avaliacaoPorUsuario(@PathVariable("codigoUsuario") Long codigoUsuario) {
        List<Avaliacao> avaliacaos = avaliacaoRepository.avaliacaoPorUsuario(codigoUsuario);
        avaliacaos.forEach(c -> {
            c.add(linkTo(methodOn(AvaliacaoController.class).read(c.getCode())).withSelfRel());
        });
        return new ResponseEntity<>(avaliacaos, HttpStatus.OK);
    }
}

