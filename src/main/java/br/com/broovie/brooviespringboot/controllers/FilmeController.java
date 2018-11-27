package br.com.broovie.brooviespringboot.controllers;

import br.com.broovie.brooviespringboot.exceptions.ResourceNotFoundException;
import br.com.broovie.brooviespringboot.interfaces.GenericOperations;
import br.com.broovie.brooviespringboot.models.Filme;
import br.com.broovie.brooviespringboot.repositories.AvaliacaoRepository;
import br.com.broovie.brooviespringboot.repositories.FilmeRepository;
import br.com.broovie.brooviespringboot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
        result.orElseThrow(() -> new ResourceNotFoundException(Filme.class, "code",o.getCode()));
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
        throw new ResourceNotFoundException(Filme.class, "code",code);
    }

    @Override
    @DeleteMapping(path = "/filme/{code}")
    public HttpEntity<Filme> delete(@PathVariable(value = "code") long code) {
        Optional<Filme> result = filmeRepository.findById(code);
        result.ifPresent(filme -> {
            filme.setExcluido(true);
            filmeRepository.save(filme);
        });
        result.orElseThrow(() -> new ResourceNotFoundException(Filme.class, "code",code));
        result.get().add(linkTo(methodOn(FilmeController.class).read(code)).withSelfRel());
        return new ResponseEntity<>(result.get(), HttpStatus.CREATED);
    }

    @Override
    @GetMapping(path = "/filmes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HttpEntity<List<Filme>> read() {
        List<Filme> filmes = filmeRepository.findAll();
        filmes.forEach(f -> {
            f.add(linkTo(methodOn(FilmeController.class).read(f.getCode())).withSelfRel());
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

    public byte[] scale(byte[] fileData) {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        try {
            BufferedImage img = ImageIO.read(in);
            Image scaledImage = img.getScaledInstance(img.getWidth() / 2, img.getHeight() / 2, Image.SCALE_SMOOTH);
            BufferedImage imageBuff = new BufferedImage(img.getWidth() / 2, img.getHeight() / 2, BufferedImage.TYPE_INT_RGB);
            imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ImageIO.write(imageBuff, "jpg", buffer);
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("IOException in scale");
        }
    }
}

