package br.com.broovie.brooviespringboot.services;

import br.com.broovie.brooviespringboot.models.Usuario;
import br.com.broovie.brooviespringboot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNomeUsuario(login).get();
        if (usuario != null) {
            return User.withUsername(usuario.getNomeUsuario()).password(usuario.getSenha()).roles("CLIENT").build();
        } else {
            throw new UsernameNotFoundException("Não foi possível encontrar o usuário " + login);
        }
    }
}