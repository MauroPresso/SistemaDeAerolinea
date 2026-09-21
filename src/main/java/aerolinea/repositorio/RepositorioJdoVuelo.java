package aerolinea.repositorio;

import aerolinea.dominio.Pasajero;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Tripulante;
import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloCharter;
import aerolinea.dominio.VueloInternacional;
import aerolinea.dominio.VueloNacional;
import aerolinea.excepcion.VueloNoDisponibleException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

public class RepositorioJdoVuelo
        implements IRepositorio<Vuelo> {

    @Override
    public void guardar(List<Vuelo> elementos)
            throws IOException {

        if (elementos == null) {
            throw new IllegalArgumentException(
                    "La lista de vuelos no puede ser nula.");
        }

        PersistenceManagerFactory pmf =
                JdoConfiguracion.crearFactory();

        try {
            eliminarVuelosPersistidos(pmf);
            persistirFotografia(pmf, elementos);
        } finally {
            pmf.close();
        }
    }

    @Override
    public List<Vuelo> consultar()
            throws IOException {

        PersistenceManagerFactory pmf =
                JdoConfiguracion.crearFactory();

        PersistenceManager pm =
                pmf.getPersistenceManager();

        Transaction tx =
                pm.currentTransaction();

        try {
            tx.begin();

            Map<Integer, Persona> personasTransitorias =
                    new LinkedHashMap<>();

            List<Vuelo> resultado =
                    new ArrayList<>();

            cargarVuelosConcretos(
                    pm,
                    VueloNacional.class,
                    resultado,
                    personasTransitorias);

            cargarVuelosConcretos(
                    pm,
                    VueloInternacional.class,
                    resultado,
                    personasTransitorias);

            cargarVuelosConcretos(
                    pm,
                    VueloCharter.class,
                    resultado,
                    personasTransitorias);

            tx.commit();
            return resultado;

        } catch (IOException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }

            throw new IOException(
                    "No se pudieron consultar "
                            + "los vuelos mediante JDO.",
                    e);
        } finally {
            pm.close();
            pmf.close();
        }
    }

    private <T extends Vuelo> void cargarVuelosConcretos(
            PersistenceManager pm,
            Class<T> tipo,
            List<Vuelo> resultado,
            Map<Integer, Persona> personasTransitorias)
            throws IOException {

        Extent<T> extent =
                pm.getExtent(tipo, false);

        try {
            for (T persistido : extent) {
                Vuelo copia =
                        clonarVueloSinRelaciones(
                                persistido);

                for (Pasajero pasajero :
                        persistido.getPasajeros()) {

                    Pasajero copiaPasajero =
                            (Pasajero)
                                    personasTransitorias
                                            .computeIfAbsent(
                                                    pasajero.getDni(),
                                                    dni ->
                                                            RepositorioJdoPersona
                                                                    .clonarPersona(
                                                                            pasajero));

                    try {
                        copia.reservarPasajero(
                                copiaPasajero);
                    } catch (
                            VueloNoDisponibleException e) {

                        throw new IOException(
                                "No se pudo reconstruir "
                                        + "la reserva del vuelo "
                                        + persistido.getNumero(),
                                e);
                    }
                }

                for (Tripulante tripulante :
                        persistido.getTripulacion()) {

                    Tripulante copiaTripulante =
                            (Tripulante)
                                    personasTransitorias
                                            .computeIfAbsent(
                                                    tripulante.getDni(),
                                                    dni ->
                                                            RepositorioJdoPersona
                                                                    .clonarPersona(
                                                                            tripulante));

                    copia.agregarTripulante(
                            copiaTripulante);
                }

                copia.setEstado(
                        persistido.getEstado());

                resultado.add(copia);
            }
        } finally {
            extent.closeAll();
        }
    }

    private void eliminarVuelosPersistidos(
            PersistenceManagerFactory pmf)
            throws IOException {

        PersistenceManager pm =
                pmf.getPersistenceManager();

        Transaction tx =
                pm.currentTransaction();

        try {
            tx.begin();

            pm.newQuery(VueloNacional.class)
                    .deletePersistentAll();

            pm.newQuery(VueloInternacional.class)
                    .deletePersistentAll();

            pm.newQuery(VueloCharter.class)
                    .deletePersistentAll();

            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }

            throw new IOException(
                    "No se pudo limpiar "
                            + "la fotografia anterior "
                            + "de vuelos.",
                    e);
        } finally {
            pm.close();
        }
    }

    private void persistirFotografia(
            PersistenceManagerFactory pmf,
            List<Vuelo> elementos)
            throws IOException {

        PersistenceManager pm =
                pmf.getPersistenceManager();

        Transaction tx =
                pm.currentTransaction();

        try {
            tx.begin();

            Map<Integer, Persona> personasGestionadas =
                    cargarPersonasGestionadas(pm);

            for (Vuelo origen : elementos) {
                Vuelo destino =
                        clonarVueloSinRelaciones(
                                origen);

                for (Pasajero pasajero :
                        origen.getPasajeros()) {

                    Pasajero gestionado =
                            (Pasajero)
                                    obtenerOCrearPersona(
                                            pm,
                                            personasGestionadas,
                                            pasajero);

                    destino.reservarPasajero(
                            gestionado);
                }

                for (Tripulante tripulante :
                        origen.getTripulacion()) {

                    Tripulante gestionado =
                            (Tripulante)
                                    obtenerOCrearPersona(
                                            pm,
                                            personasGestionadas,
                                            tripulante);

                    destino.agregarTripulante(
                            gestionado);
                }

                destino.setEstado(
                        origen.getEstado());

                pm.makePersistent(destino);
            }

            tx.commit();

        } catch (VueloNoDisponibleException e) {
            if (tx.isActive()) {
                tx.rollback();
            }

            throw new IOException(
                    "No se pudo reconstruir "
                            + "una reserva al persistir.",
                    e);

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }

            throw new IOException(
                    "No se pudieron guardar "
                            + "los vuelos mediante JDO.",
                    e);
        } finally {
            pm.close();
        }
    }

    private Map<Integer, Persona>
    cargarPersonasGestionadas(
            PersistenceManager pm) {

        Map<Integer, Persona> personas =
                new LinkedHashMap<>();

        cargarPersonasDeTipo(
                pm,
                Pasajero.class,
                personas);

        cargarPersonasDeTipo(
                pm,
                Tripulante.class,
                personas);

        return personas;
    }

    private <T extends Persona> void cargarPersonasDeTipo(
            PersistenceManager pm,
            Class<T> tipo,
            Map<Integer, Persona> destino) {

        Extent<T> extent =
                pm.getExtent(tipo, false);

        try {
            for (T persona : extent) {
                destino.put(
                        persona.getDni(),
                        persona);
            }
        } finally {
            extent.closeAll();
        }
    }

    private Persona obtenerOCrearPersona(
            PersistenceManager pm,
            Map<Integer, Persona> personas,
            Persona origen) {

        Persona gestionada =
                personas.get(origen.getDni());

        if (gestionada == null) {
            gestionada =
                    RepositorioJdoPersona
                            .clonarPersona(origen);

            pm.makePersistent(gestionada);

            personas.put(
                    gestionada.getDni(),
                    gestionada);

            return gestionada;
        }

        RepositorioJdoPersona
                .actualizarPersona(
                        gestionada,
                        origen);

        return gestionada;
    }

    private Vuelo clonarVueloSinRelaciones(
            Vuelo origen) {

        if (origen instanceof VueloNacional) {
            VueloNacional nacional =
                    (VueloNacional) origen;

            return new VueloNacional(
                    nacional.getNumero(),
                    nacional.getOrigen(),
                    nacional.getDestino(),
                    nacional.getFecha(),
                    nacional.getCapacidad(),
                    nacional.getProvinciaDestino());
        }

        if (origen instanceof VueloInternacional) {
            VueloInternacional internacional =
                    (VueloInternacional) origen;

            return new VueloInternacional(
                    internacional.getNumero(),
                    internacional.getOrigen(),
                    internacional.getDestino(),
                    internacional.getFecha(),
                    internacional.getCapacidad(),
                    internacional.getPaisDestino(),
                    internacional.isRequierePasaporte());
        }

        if (origen instanceof VueloCharter) {
            VueloCharter charter =
                    (VueloCharter) origen;

            return new VueloCharter(
                    charter.getNumero(),
                    charter.getOrigen(),
                    charter.getDestino(),
                    charter.getFecha(),
                    charter.getCapacidad(),
                    charter.getEmpresaContratante(),
                    charter.getCostoTotal());
        }

        throw new IllegalArgumentException(
                "Tipo de Vuelo no soportado: "
                        + origen.getClass().getName());
    }
}
