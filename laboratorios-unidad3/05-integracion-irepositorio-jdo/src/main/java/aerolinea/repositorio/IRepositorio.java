package aerolinea.repositorio;

import java.io.IOException;
import java.util.List;

/**
 * Contrato equivalente al usado por SistemaDeAerolinea.
 *
 * @param <T> tipo administrado por el repositorio
 */
public interface IRepositorio<T> {

    void guardar(List<T> elementos) throws IOException;

    List<T> consultar() throws IOException, ClassNotFoundException;
}
