package aerolinea.repositorio;

import aerolinea.dominio.Pasajero;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Tripulante;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

public class RepositorioJdoPersona
        implements IRepositorio<Persona> {

    @Override
    public void guardar(List<Persona> elementos)
            throws IOException {

        if (elementos == null) {
            throw new IllegalArgumentException(
                    "La lista de personas no puede ser nula.");
        }

        PersistenceManagerFactory pmf =
                JdoConfiguracion.crearFactory();

        PersistenceManager pm =
                pmf.getPersistenceManager();

        Transaction tx =
                pm.currentTransaction();

        try {
            tx.begin();

            Map<Integer, Persona> existentes =
                    cargarPersonasGestionadas(pm);

            Map<Integer, Persona> recibidas =
                    new LinkedHashMap<>();

            for (Persona persona : elementos) {
                recibidas.put(
                        persona.getDni(),
                        persona);

                Persona gestionada =
                        existentes.get(
                                persona.getDni());

                if (gestionada == null) {
                    pm.makePersistent(
                            clonarPersona(persona));
                } else {
                    actualizarPersona(
                            gestionada,
                            persona);
                }
            }

            for (Persona gestionada :
                    existentes.values()) {

                if (!recibidas.containsKey(
                        gestionada.getDni())) {

                    pm.deletePersistent(
                            gestionada);
                }
            }

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }

            throw new IOException(
                    "No se pudieron guardar "
                            + "las personas mediante JDO.",
                    e);
        } finally {
            pm.close();
            pmf.close();
        }
    }

    @Override
    public List<Persona> consultar()
            throws IOException {

        PersistenceManagerFactory pmf =
                JdoConfiguracion.crearFactory();

        PersistenceManager pm =
                pmf.getPersistenceManager();

        Transaction tx =
                pm.currentTransaction();

        try {
            tx.begin();

            Map<Integer, Persona> gestionadas =
                    cargarPersonasGestionadas(pm);

            List<Persona> resultado =
                    new ArrayList<>();

            for (Persona persona :
                    gestionadas.values()) {

                resultado.add(
                        clonarPersona(persona));
            }

            tx.commit();

            return resultado;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }

            throw new IOException(
                    "No se pudieron consultar "
                            + "las personas mediante JDO.",
                    e);
        } finally {
            pm.close();
            pmf.close();
        }
    }

    private Map<Integer, Persona>
    cargarPersonasGestionadas(
            PersistenceManager pm) {

        Map<Integer, Persona> personas =
                new LinkedHashMap<>();

        cargarExtent(pm, Pasajero.class, personas);
        cargarExtent(pm, Tripulante.class, personas);

        return personas;
    }

    private <T extends Persona> void cargarExtent(
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

    static Persona clonarPersona(Persona persona) {
        if (persona instanceof Pasajero) {
            Pasajero pasajero =
                    (Pasajero) persona;

            return new Pasajero(
                    pasajero.getDni(),
                    pasajero.getNombre(),
                    pasajero.getApellido(),
                    pasajero.getNumeroPasaporte());
        }

        if (persona instanceof Tripulante) {
            Tripulante tripulante =
                    (Tripulante) persona;

            return new Tripulante(
                    tripulante.getDni(),
                    tripulante.getNombre(),
                    tripulante.getApellido(),
                    tripulante.getLegajo(),
                    tripulante.getRol());
        }

        throw new IllegalArgumentException(
                "Tipo de Persona no soportado: "
                        + persona.getClass().getName());
    }

    static void actualizarPersona(
            Persona destino,
            Persona origen) {

        if (!destino.getClass()
                .equals(origen.getClass())) {

            throw new IllegalArgumentException(
                    "El DNI "
                            + origen.getDni()
                            + " ya existe con otro "
                            + "tipo de persona.");
        }

        destino.setNombre(origen.getNombre());
        destino.setApellido(origen.getApellido());

        if (destino instanceof Pasajero) {
            ((Pasajero) destino)
                    .setNumeroPasaporte(
                            ((Pasajero) origen)
                                    .getNumeroPasaporte());
        }

        if (destino instanceof Tripulante) {
            Tripulante destinoTripulante =
                    (Tripulante) destino;

            Tripulante origenTripulante =
                    (Tripulante) origen;

            destinoTripulante.setLegajo(
                    origenTripulante.getLegajo());

            destinoTripulante.setRol(
                    origenTripulante.getRol());
        }
    }
}
