package aerolinea.main;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Vuelo;
import aerolinea.repositorio.IRepositorio;
import aerolinea.repositorio.RepositorioArchivo;
import aerolinea.repositorio.RepositorioJdoPersona;
import aerolinea.repositorio.RepositorioJdoVuelo;
import aerolinea.servicio.Servicio;
import aerolinea.ui.Menu;
import aerolinea.ui.Ventana;

import javax.swing.SwingUtilities;
import java.util.Arrays;

/**
 * Punto de entrada del Sistema de Aerolinea.
 *
 * <p>Modos de persistencia:</p>
 * <ul>
 *     <li>Por defecto: archivos .dat.</li>
 *     <li>Con --orm: DataNucleus/JDO + H2.</li>
 * </ul>
 *
 * <p>La opcion --consola puede combinarse con cualquiera de los dos modos.</p>
 */
public class Main {

    public static void main(String[] args) {

        boolean modoOrm =
                Arrays.stream(args)
                        .anyMatch(
                                "--orm"::equalsIgnoreCase);

        boolean modoConsola =
                Arrays.stream(args)
                        .anyMatch(
                                "--consola"::equalsIgnoreCase);

        IRepositorio<Vuelo> repositorioVuelos;
        IRepositorio<Persona> repositorioPersonas;

        if (modoOrm) {
            repositorioVuelos =
                    new RepositorioJdoVuelo();

            repositorioPersonas =
                    new RepositorioJdoPersona();

            System.out.println(
                    "Persistencia: DataNucleus/JDO + H2");
        } else {
            repositorioVuelos =
                    new RepositorioArchivo<>(
                            "data/vuelos.dat");

            repositorioPersonas =
                    new RepositorioArchivo<>(
                            "data/personas.dat");

            System.out.println(
                    "Persistencia: archivos .dat");
        }

        Servicio<Vuelo> servicioVuelos =
                new Servicio<>(repositorioVuelos);

        Servicio<Persona> servicioPersonas =
                new Servicio<>(repositorioPersonas);

        Aerolinea aerolinea =
                new Aerolinea(
                        "Aerolinea IFES",
                        servicioVuelos.listar(),
                        servicioPersonas.listar());

        if (modoConsola) {
            Menu menu =
                    new Menu(
                            aerolinea,
                            servicioVuelos,
                            servicioPersonas);

            menu.iniciar();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            Ventana ventana =
                    new Ventana(
                            aerolinea,
                            servicioVuelos,
                            servicioPersonas);

            ventana.setVisible(true);
        });
    }
}
