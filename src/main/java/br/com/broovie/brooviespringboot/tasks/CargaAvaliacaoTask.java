package br.com.broovie.brooviespringboot.tasks;

import br.com.broovie.brooviespringboot.models.Avaliacao;
import br.com.broovie.brooviespringboot.models.Filme;
import br.com.broovie.brooviespringboot.models.Usuario;
import br.com.broovie.brooviespringboot.repositories.AvaliacaoRepository;
import br.com.broovie.brooviespringboot.repositories.FilmeRepository;
import br.com.broovie.brooviespringboot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CargaAvaliacaoTask {
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    AvaliacaoRepository avaliacaoRepository;
    @Autowired
    FilmeRepository filmeRepository;

    //@Scheduled(initialDelay = 1000, fixedRate = Long.MAX_VALUE)
    public void cargaDeAvaliacao() {
        try {
            Random random = new Random();
            List<Usuario> usuarios = usuarioRepository.pesquisar("Aleatorio ", "");
            List<Filme> filmes = filmeRepository.findAll();
            usuarios.forEach(usuario -> {
                Set<Filme> filmesUsuario = new HashSet<>();
                int qtdFilmes = random.nextInt(271) + 30;
                while (filmesUsuario.size() < qtdFilmes) {
                    filmesUsuario.add(filmes.get(random.nextInt(filmes.size())));
                }
                List<Avaliacao> avaliacoes = new ArrayList<>();
                filmesUsuario.forEach(filme -> {
                    int nota = random.nextInt(9) + 1;
                    avaliacoes.add(Avaliacao.builder()
                            .usuario(usuario)
                            .filme(filme)
                            .nota(Avaliacao.Nota.values()[nota <= 5 ? nota : 5])
                            .build());
                });
                avaliacaoRepository.saveAll(avaliacoes);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
