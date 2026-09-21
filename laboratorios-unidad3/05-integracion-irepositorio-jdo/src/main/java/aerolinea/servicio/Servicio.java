package aerolinea.servicio;

import aerolinea.repositorio.IRepositorio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio generico equivalente al patron utilizado en el proyecto principal.
 */
public class Servicio<T> {

    private final IRepositorio<T> repositorio;
    private final List<T> elementos;

    public Servicio(IRepositorio<T> repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo.");
        }

        this.repositorio = repositorio;
        this.elementos = new ArrayList<>();

        try {
            List<T> recuperados = repositorio.consultar();
            if (recuperados != null) {
                elementos.addAll(recuperados);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                    "No se pudieron recuperar los elementos persistidos: "
                            + e.getMessage());
        }
    }

    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento no puede ser nulo.");
        }
        elementos.add(elemento);
    }

    public List<T> listar() {
        return elementos;
    }

    public void reemplazarTodos(List<T> nuevosElementos) {
        if (nuevosElementos == null) {
            throw new IllegalArgumentException("La lista no puede ser nula.");
        }

        elementos.clear();
        elementos.addAll(nuevosElementos);
    }

    public void guardar() throws IOException {
        repositorio.guardar(new ArrayList<>(elementos));
    }

    public IRepositorio<T> getRepositorio() {
        return repositorio;
    }
}
