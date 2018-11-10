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
    public HttpEntity<Usuario> create(@RequestBody Usuario o) {
        usuarioRepository.save(o);
        o.add(linkTo(methodOn(UsuarioController.class).read(o.getCode())).withSelfRel());
        o.add(linkTo(methodOn(UsuarioController.class).amigos(o.getCode())).withRel("amigos"));
        return new ResponseEntity<>(o, o.getCode() != null ? HttpStatus.CREATED : HttpStatus.NO_CONTENT);
    }

    @Override
    @PutMapping(path = "/usuario", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Usuario> update(@RequestBody Usuario o) {
        Optional<Usuario> result = usuarioRepository.findById(o.getCode());
        result.ifPresent(usuario -> {
            usuario.setAmigos(o.getAmigos());
            usuario.setDataNascimento(o.getDataNascimento());
            usuario.setEmail(o.getEmail());
            usuario.setGeneros(o.getGeneros());
            usuario.setNome(o.getNome());
            usuario.setNomeUsuario(o.getNomeUsuario());
            usuario.setPais(o.getPais());
            usuario.setSenha(o.getSenha());
            usuarioRepository.save(usuario);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, o.getCode()));
        o.add(linkTo(methodOn(UsuarioController.class).read(o.getCode())).withSelfRel());
        o.add(linkTo(methodOn(UsuarioController.class).amigos(o.getCode())).withRel("amigos"));
        return new ResponseEntity<>(o, HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "/usuario/{code}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Usuario> read(@PathVariable(value = "code") long code) {
        Optional<Usuario> result = usuarioRepository.findById(code);
        if (result.isPresent()) {
            Usuario entity = result.get();
            entity.add(linkTo(methodOn(UsuarioController.class).read(entity.getCode())).withSelfRel());
            entity.add(linkTo(methodOn(UsuarioController.class).amigos(entity.getCode())).withRel("amigos"));
            entity.add(linkTo(methodOn(UsuarioController.class).read()).withRel("all"));
            return new ResponseEntity<>(entity, HttpStatus.OK);
        }
        throw new ResourceNotFoundException(Usuario.class, code);
    }

    @Override
    @DeleteMapping(path = "/usuario/{code}")
    public HttpEntity<Usuario> delete(@PathVariable(value = "code") long code) {
        Optional<Usuario> result = usuarioRepository.findById(code);
        result.ifPresent(genero -> {
            genero.setExcluido(true);
            usuarioRepository.save(genero);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, code));
        result.get().add(linkTo(methodOn(UsuarioController.class).read(code)).withSelfRel());
        return new ResponseEntity<>(result.get(), HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "/usuarios", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Usuario>> read() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(o -> {
            o.add(linkTo(methodOn(UsuarioController.class).read(o.getCode())).withSelfRel());
            o.add(linkTo(methodOn(UsuarioController.class).amigos(o.getCode())).withRel("amigos"));
        });
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping(path = "/usuario/pesquisar", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Usuario>> pesquisar(
            @RequestParam(value = "nome", defaultValue = "", required = false) String nome,
            @RequestParam(value = "nomeUsuario", defaultValue = "", required = false) String nomeUsuario) {
        if (nome.isEmpty() && nomeUsuario.isEmpty())
            throw new BadRequestException("Enter one of the parameters: 'nome' or 'nomeUsuario'");
        List<Usuario> usuarios = usuarioRepository.pesquisar(nome, nomeUsuario);
        usuarios.forEach(o -> {
            o.add(linkTo(methodOn(UsuarioController.class).read(o.getCode())).withSelfRel());
            o.add(linkTo(methodOn(UsuarioController.class).amigos(o.getCode())).withRel("amigos"));
        });
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping(path = "/usuario/autenticar", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Usuario> autenticar(@RequestParam(value = "nomeUsuario") String nomeUsuario, @RequestParam(value = "senha") String senha) {
        Usuario usuario = usuarioRepository.autenticar(nomeUsuario, senha);
        if (usuario != null) {
            usuario.add(linkTo(methodOn(UsuarioController.class).read(usuario.getCode())).withSelfRel());
            usuario.add(linkTo(methodOn(UsuarioController.class).amigos(usuario.getCode())).withRel("amigos"));
            usuario.add(linkTo(methodOn(UsuarioController.class).read()).withRel("all"));
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("nomeUsuario or senha not found");
    }

    @GetMapping(path = "/usuario/{code}/amigos", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Usuario>> amigos(@PathVariable(value = "code") long code) {
        List<Usuario> usuarios = usuarioRepository.amigos(code);
        usuarios.forEach(o -> {
            o.add(linkTo(methodOn(UsuarioController.class).read(o.getCode())).withSelfRel());
            o.add(linkTo(methodOn(UsuarioController.class).amigos(o.getCode())).withRel("amigos"));
        });
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
}

