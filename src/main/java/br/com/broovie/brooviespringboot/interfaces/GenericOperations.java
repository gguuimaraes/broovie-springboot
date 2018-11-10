package br.com.broovie.brooviespringboot.interfaces;

import br.com.broovie.brooviespringboot.models.Filme;
import org.springframework.http.HttpEntity;

import java.util.List;

public interface GenericOperations<E> {
    HttpEntity<E> create(E o);

    HttpEntity<E> update(E o);

    HttpEntity<E> read(long code);

    HttpEntity<List<E>> read();

    HttpEntity<E> delete(long code);
}
