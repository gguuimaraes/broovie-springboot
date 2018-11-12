package br.com.broovie.brooviespringboot.controllers;

import br.com.broovie.brooviespringboot.exceptions.ResourceNotFoundException;
import br.com.broovie.brooviespringboot.models.Amizade;
import br.com.broovie.brooviespringboot.models.Usuario;
import br.com.broovie.brooviespringboot.repositories.AmizadeRepository;
import br.com.broovie.brooviespringboot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
public class AmizadeController {
    @Autowired
    AmizadeRepository amizadeRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping(path = "usuario/{code}/amigos", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Usuario>> amigos(@PathVariable long code) {
        List<Usuario> amigos = amizadeRepository.amigos(code);
        amigos.addAll(amizadeRepository.amigos2(code));
        return new ResponseEntity<>(amigos, HttpStatus.OK);
    }

    @GetMapping(path = "amizades/{code}/pendentes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Usuario>> solicitacoesPendentes(@PathVariable long code) {
        return new ResponseEntity<>(amizadeRepository.solicitacoesPendentes(code), HttpStatus.OK);
    }

    @PostMapping(path = "usuario/{code}/amigos", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public HttpEntity<List<Usuario>> adicionarAceitarAmigo(@PathVariable long code, @RequestBody Usuario amigo) {
        if (!amigo.getCode().equals(code)) {
            Optional<Usuario> optUsuario = usuarioRepository.findById(code);
            optUsuario.ifPresent(u -> {
                Optional<Amizade> optAmizade = amizadeRepository.amizade(code, amigo.getCode());
                if (optAmizade.isPresent()) {
                    if (optAmizade.get().getSituacao() == Amizade.Situacao.PENDENTE) {
                        optAmizade.get().setSituacao(Amizade.Situacao.APROVADA);
                        optAmizade.get().setDataConfirmacao(new Date());
                        amizadeRepository.save(optAmizade.get());
                    }
                } else {
                    Optional<Usuario> optAmigo = usuarioRepository.findById(amigo.getCode());
                    optAmigo.ifPresent(a -> {
                        Amizade amizade = Amizade.builder()
                                .solicitado(a)
                                .solicitante(u)
                                .dataSolicitacao(new Date())
                                .situacao(Amizade.Situacao.PENDENTE)
                                .build();
                        amizadeRepository.save(amizade);
                    });
                    optAmigo.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, amigo.getCode()));
                }
            });
            optUsuario.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, code));
        }
        return amigos(code);
    }

    @DeleteMapping(path = "usuario/{code}/amigos", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public HttpEntity<List<Usuario>> removerRejeitarAmigo(@PathVariable long code, @RequestBody Usuario amigo) {
        if (!amigo.getCode().equals(code)) {
            Optional<Usuario> optUsuario = usuarioRepository.findById(code);
            optUsuario.ifPresent(u -> {
                Optional<Amizade> optAmizade = amizadeRepository.amizade(code, amigo.getCode());
                if (optAmizade.isPresent()) {
                    optAmizade.get().setSituacao(Amizade.Situacao.REJEITADA);
                    optAmizade.get().setDataConfirmacao(new Date());
                    amizadeRepository.save(optAmizade.get());
                } else {
                    optAmizade = amizadeRepository.amizade(amigo.getCode(), code);
                    if (optAmizade.isPresent()) {
                        optAmizade.get().setSituacao(Amizade.Situacao.REJEITADA);
                        optAmizade.get().setDataConfirmacao(new Date());
                        amizadeRepository.save(optAmizade.get());
                    }
                }
            });
            optUsuario.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, code));
        }
        return amigos(code);
    }
}

