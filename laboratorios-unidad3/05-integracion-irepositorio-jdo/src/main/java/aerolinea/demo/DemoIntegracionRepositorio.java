package aerolinea.demo;

import aerolinea.orm.VueloPersistente;
import aerolinea.repositorio.IRepositorio;
import aerolinea.repositorio.RepositorioJdoVuelo;
import aerolinea.servicio.Servicio;

import java.util.List;

/**
 * Demuestra la misma composicion que usa SistemaDeAerolinea:
 *
 * IRepositorio<T> -> Servicio<T>
 *
 * pero sustituyendo el repositorio de archivos por una implementacion JDO.
 */
public final class DemoIntegracionRepositorio {

    private DemoIntegracionRepositorio() {
    }

    public static void main(String[] args) throws Exception {
        try (RepositorioJdoVuelo repositorioJdo =
                     new RepositorioJdoVuelo("datanucleus.properties")) {

            IRepositorio<VueloPersistente> repositorio = repositorioJdo;

            Servicio<VueloPersistente> servicio =
                    new Servicio<>(repositorio);

            servicio.reemplazarTodos(
                    List.of(
                            new VueloPersistente(
                                    "AR5001",
                                    "Neuquen",
                                    "Buenos Aires"),
                            new VueloPersistente(
                                    "AR5002",
                                    "Buenos Aires",
                                    "Ushuaia")));

            servicio.guardar();

            System.out.println(
                    "Servicio 1 guardo "
                            + servicio.listar().size()
                            + " vuelos mediante IRepositorio.");
        }

        /*
         * Se crea un repositorio y un servicio completamente nuevos.
         * Si los vuelos reaparecen, sabemos que provienen de H2 y no de la
         * lista en memoria del primer Servicio.
         */
        try (RepositorioJdoVuelo repositorioJdo =
                     new RepositorioJdoVuelo("datanucleus.properties")) {

            Servicio<VueloPersistente> servicioRecargado =
                    new Servicio<>(repositorioJdo);

            System.out.println();
            System.out.println(
                    "Nuevo Servicio recupera desde H2:");

            for (VueloPersistente vuelo : servicioRecargado.listar()) {
                System.out.println("- " + vuelo);
            }

            servicioRecargado.agregar(
                    new VueloPersistente(
                            "AR5003",
                            "Mendoza",
                            "Salta"));

            servicioRecargado.guardar();

            System.out.println();
            System.out.println(
                    "Se agrego AR5003 y se guardo nuevamente.");
        }

        try (RepositorioJdoVuelo repositorioJdo =
                     new RepositorioJdoVuelo("datanucleus.properties")) {

            Servicio<VueloPersistente> verificacion =
                    new Servicio<>(repositorioJdo);

            System.out.println();
            System.out.println(
                    "Verificacion final desde un tercer Servicio:");

            for (VueloPersistente vuelo : verificacion.listar()) {
                System.out.println("- " + vuelo);
            }
        }
    }
}
