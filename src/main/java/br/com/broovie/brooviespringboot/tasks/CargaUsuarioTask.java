package br.com.broovie.brooviespringboot.tasks;

import br.com.broovie.brooviespringboot.models.Usuario;
import br.com.broovie.brooviespringboot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class CargaUsuarioTask {
    @Autowired
    UsuarioRepository usuarioRepository;

    private Integer contagem = 1;

    //@Scheduled(initialDelay = 1000, fixedRate = Long.MAX_VALUE)
    public void cargaDeUsuario() {
        try {
            Collection<Usuario> usuarios = new ArrayList<>();
            for (; contagem < 590; contagem++) {
                usuarios.add(Usuario.builder()
                        .nome("Aleatorio " + contagem)
                        .nomeUsuario("A" + contagem.toString())
                        .senha("A" + contagem.toString())
                        .build());
            }
            usuarioRepository.saveAll(usuarios);
            System.out.println("Todos os usuarios foram cadastrados");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
