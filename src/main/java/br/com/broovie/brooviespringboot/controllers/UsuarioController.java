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
        u.add(linkTo(methodOn(UsuarioController.class).amigos(u.getCode())).withRel("amigos"));
        return new ResponseEntity<>(u, u.getCode() != null ? HttpStatus.CREATED : HttpStatus.NO_CONTENT);
    }

    @Override
    @PutMapping(path = "/usuario", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<Usuario> update(@RequestBody Usuario u) {
        Optional<Usuario> result = usuarioRepository.findById(u.getCode());
        result.ifPresent(usuario -> {
            usuario.setAmigos(u.getAmigos());
            usuario.setDataNascimento(u.getDataNascimento());
            usuario.setEmail(u.getEmail());
            usuario.setGeneros(u.getGeneros());
            usuario.setNome(u.getNome());
            usuario.setNomeUsuario(u.getNomeUsuario());
            usuario.setPais(u.getPais());
            usuario.setSenha(u.getSenha());
            usuarioRepository.save(usuario);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, u.getCode()));
        u.add(linkTo(methodOn(UsuarioController.class).read(u.getCode())).withSelfRel());
        u.add(linkTo(methodOn(UsuarioController.class).amigos(u.getCode())).withRel("amigos"));
        return new ResponseEntity<>(u, HttpStatus.CREATED);
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
        result.ifPresent(usuario -> {
            usuario.setExcluido(true);
            usuarioRepository.save(usuario);
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

    @GetMapping(path = "/usuarios/pesquisar", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
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
        usuarios.forEach(u -> {
            u.add(linkTo(methodOn(UsuarioController.class).read(u.getCode())).withSelfRel());
            u.add(linkTo(methodOn(UsuarioController.class).amigos(u.getCode())).withRel("amigos"));
        });
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @PostMapping(path = "/usuario/{code}/amigos", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Usuario>> adicionarAmigo(@PathVariable(value = "code") long code, @RequestBody Usuario amigo) {
        if (!amigo.getCode().equals(code)) {
            Optional<Usuario> result = usuarioRepository.findById(code);
            result.ifPresent(usuario -> {
                usuario.setAmigos(usuarioRepository.amigos(code));
                boolean isAmigo = false;
                for (Usuario usuarioAmigo : usuario.getAmigos()) {
                    if (usuarioAmigo.getCode().equals(amigo.getCode())) {
                        isAmigo = true;
                        break;
                    }
                }
                if (!isAmigo) {
                    Optional<Usuario> result2 = usuarioRepository.findById(amigo.getCode());
                    result2.ifPresent(amigo2 -> {
                        usuario.getAmigos().add(amigo2);
                    });
                    result2.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, amigo.getCode()));
                    usuarioRepository.save(usuario);
                }
            });
            result.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, code));
        }
        return amigos(code);
    }

    @DeleteMapping(path = "/usuario/{code}/amigos", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Usuario>> removerAmigo(@PathVariable(value = "code") long code, @RequestBody Usuario amigo) {
        if (!amigo.getCode().equals(code)) {
            Optional<Usuario> result = usuarioRepository.findById(code);
            result.ifPresent(usuario -> {
                usuario.setAmigos(usuarioRepository.amigos(code));
                boolean isAmigo = false;
                for (Usuario usuarioAmigo : usuario.getAmigos()) {
                    if (usuarioAmigo.getCode().equals(amigo.getCode())) {
                        isAmigo = true;
                        break;
                    }
                }
                if (isAmigo) {
                    Optional<Usuario> result2 = usuarioRepository.findById(amigo.getCode());
                    result2.ifPresent(amigo2 -> {
                        usuario.getAmigos().remove(amigo2);
                    });
                    result2.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, amigo.getCode()));
                    usuarioRepository.save(usuario);
                }
            });
            result.orElseThrow(() -> new ResourceNotFoundException(Usuario.class, code));
        }
        return amigos(code);
    }
}

