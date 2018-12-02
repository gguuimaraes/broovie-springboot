package br.com.broovie.brooviespringboot.tasks;

import br.com.broovie.brooviespringboot.repositories.FilmeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class VerificarFotosTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerificarFotosTask.class);

    @Autowired
    FilmeRepository filmeRepository;

    private static final String FOTO_PATH = "C:\\broovie\\images\\";

    @Scheduled(cron = "0 0 12 * * ?")
    public void verificar() {
        try {
            verificarFilmesSemFoto();
            verificarFotosSemFilme();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void verificarFilmesSemFoto() {
        this.filmeRepository.findAll().parallelStream().forEach(filme -> {
            if (!new java.io.File(FOTO_PATH + filme.getCode() + ".jpg").exists()) {
                LOGGER.info(String.format("Filme\t[%09d]\tsem foto.", filme.getCode()));
            }
        });
        LOGGER.info("Verificação de filmes sem foto finalizada.");
    }

    private void verificarFotosSemFilme() {
        File pastaImages = new File(FOTO_PATH);
        if (!pastaImages.exists()) return;
        File[] fotos = pastaImages.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));
        for (File foto : fotos) {
            if (!this.filmeRepository.findById(Long.parseLong(foto.getName().replace(".jpg", ""))).isPresent()) {
                LOGGER.info(String.format("Foto\t[%s]\tsem filme%s.", foto.getName(), foto.delete() ? ", excluída" : ""));
            }
        }
        LOGGER.info("Verificação de fotos sem filme finalizada.");
    }
}
