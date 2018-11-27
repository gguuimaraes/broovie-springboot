package br.com.broovie.brooviespringboot.controllers;

import br.com.broovie.brooviespringboot.exceptions.BadRequestException;
import br.com.broovie.brooviespringboot.exceptions.ResourceNotFoundException;
import br.com.broovie.brooviespringboot.interfaces.GenericOperations;
import br.com.broovie.brooviespringboot.models.Usuario;
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
public class UsuarioController implements GenericOperations<Usuario> {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    @PostMapping(path = "/usuario", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Usuario> create(@RequestBody Usuario u) {
        usuarioRepository.save(u);
        u.add(linkTo(methodOn(UsuarioController.class).read(u.getCode())).withSelfRel());
        u.add(linkTo(methodOn(AmizadeController.class).amigos(u.getCode())).withRel("amigos"));
        return new ResponseEntity<>(u, u.getCode() != null ? HttpStatus.CREATED : HttpStatus.NO_CONTENT);
    }

    @Override
    @PutMapping(path = "/usuario", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Usuario> update(@RequestBody Usuario u) {
        Optional<Usuario> result = usuarioRepository.findById(u.getCode());
        result.ifPresent(usuario -> {
            usuario.setDataNascimento(u.getDataNascimento());
            usuario.setEmail(u.getEmail());
            usuario.setGeneros(u.getGeneros());
            usuario.setNome(u.getNome());
            usuario.setNomeUsuario(u.getNomeUsuario());
            usuario.setPais(u.getPais());
            usuario.setSenha(u.getSenha());
            usuarioRepository.save(usuario);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, "code", u.getCode()));
        u.add(linkTo(methodOn(UsuarioController.class).read(u.getCode())).withSelfRel());
        u.add(linkTo(methodOn(AmizadeController.class).amigos(u.getCode())).withRel("amigos"));
        return new ResponseEntity<>(u, HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "/usuario/{code}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Usuario> read(@PathVariable(value = "code") long code) {
        Optional<Usuario> result = usuarioRepository.findById(code);
        if (result.isPresent()) {
            Usuario entity = result.get();
            entity.add(linkTo(methodOn(UsuarioController.class).read(entity.getCode())).withSelfRel());
            entity.add(linkTo(methodOn(AmizadeController.class).amigos(entity.getCode())).withRel("amigos"));
            entity.add(linkTo(methodOn(UsuarioController.class).read()).withRel("all"));
            return new ResponseEntity<>(entity, HttpStatus.OK);
        }
        throw new ResourceNotFoundException(Usuario.class, "code", code);
    }

    @Override
    @DeleteMapping(path = "/usuario/{code}")
    public HttpEntity<Usuario> delete(@PathVariable(value = "code") long code) {
        Optional<Usuario> result = usuarioRepository.findById(code);
        result.ifPresent(usuario -> {
            usuario.setExcluido(true);
            usuarioRepository.save(usuario);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, "code", code));
        result.get().add(linkTo(methodOn(UsuarioController.class).read(code)).withSelfRel());
        return new ResponseEntity<>(result.get(), HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "/usuarios", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Usuario>> read() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(o -> {
            o.add(linkTo(methodOn(UsuarioController.class).read(o.getCode())).withSelfRel());
            o.add(linkTo(methodOn(AmizadeController.class).amigos(o.getCode())).withRel("amigos"));
        });
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping(path = "/usuarios/pesquisar", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Usuario>> pesquisar(
            @RequestParam(value = "nome", defaultValue = "", required = false) String nome,
            @RequestParam(value = "nomeUsuario", defaultValue = "", required = false) String nomeUsuario) {
        if (nome.isEmpty() && nomeUsuario.isEmpty())
            throw new BadRequestException("Enter one of the parameters: 'nome' or 'nomeUsuario'");
        List<Usuario> usuarios = usuarioRepository.pesquisar(nome, nomeUsuario);
        usuarios.forEach(o -> {
            o.add(linkTo(methodOn(UsuarioController.class).read(o.getCode())).withSelfRel());
            o.add(linkTo(methodOn(AmizadeController.class).amigos(o.getCode())).withRel("amigos"));
        });
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping(path = "/usuario", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Usuario> readByNomeUsuario(@RequestParam(value = "nomeUsuario") String nomeUsuario) {
        Optional<Usuario> result = usuarioRepository.findByNomeUsuario(nomeUsuario);
        if (result.isPresent()) {
            Usuario usuario = result.get();
            usuario.add(linkTo(methodOn(UsuarioController.class).read(usuario.getCode())).withSelfRel());
            usuario.add(linkTo(methodOn(AmizadeController.class).amigos(usuario.getCode())).withRel("amigos"));
            usuario.add(linkTo(methodOn(UsuarioController.class).read()).withRel("all"));
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        }
        throw new ResourceNotFoundException(Usuario.class, "nomeUsuario", nomeUsuario);
    }
}

