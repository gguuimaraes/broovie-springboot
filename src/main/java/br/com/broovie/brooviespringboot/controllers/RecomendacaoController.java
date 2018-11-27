package br.com.broovie.brooviespringboot.controllers;

import br.com.broovie.brooviespringboot.models.Recomendacao;
import br.com.broovie.brooviespringboot.repositories.RecomendacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping
public class RecomendacaoController {
    @Autowired
    RecomendacaoRepository recomendacaoRepository;

    @GetMapping(path = "/recomendacoes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Set<Recomendacao>> read(@RequestParam("codigoUsuario") long codigoUsuario, @RequestParam("tipo") Recomendacao.TipoRecomendacao tipo) {
        Set<Recomendacao> recomendacoes = recomendacaoRepository.recomendacoesPorUsuarioTipo(codigoUsuario, tipo);
        recomendacoes.parallelStream().forEach(recomendacao -> {
            recomendacao.add(linkTo(methodOn(RecomendacaoController.class).read(codigoUsuario, tipo)).withSelfRel());
            recomendacao.add(linkTo(methodOn(UsuarioController.class).read(codigoUsuario)).withRel("usuario"));
        });
        return new ResponseEntity<>(recomendacoes, HttpStatus.OK);
    }
}