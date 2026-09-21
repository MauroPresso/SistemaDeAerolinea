package aerolinea.demo;

import aerolinea.dominio.Pasajero;
import aerolinea.dominio.Tripulante;
import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloCharter;
import aerolinea.dominio.VueloInternacional;
import aerolinea.dominio.VueloNacional;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public final class DemoRelacionesOrm {

    private DemoRelacionesOrm() {
    }

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("datanucleus.properties")) {
            props.load(in);
        }

        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props);
        try {
            limpiar(pmf);
            persistir(pmf);
            consultarDesdeVuelo(pmf);
            consultarDesdePasajero(pmf);
        } finally {
            pmf.close();
        }
    }

    private static void limpiar(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            pm.newQuery(VueloNacional.class).deletePersistentAll();
            pm.newQuery(VueloInternacional.class).deletePersistentAll();
            pm.newQuery(VueloCharter.class).deletePersistentAll();
            pm.newQuery(Pasajero.class).deletePersistentAll();
            pm.newQuery(Tripulante.class).deletePersistentAll();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            pm.close();
        }
    }

    private static void persistir(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();

            Pasajero ana = new Pasajero(30111222, "Ana", "Lopez", "AA30111222");
            Pasajero bruno = new Pasajero(32222333, "Bruno", "Gomez", "AA32222333");

            Tripulante piloto = new Tripulante(27123456, "Laura", "Perez", 9001, "Piloto");
            Tripulante tcp = new Tripulante(28456789, "Martin", "Diaz", 9002, "TCP");

            VueloNacional nacional = new VueloNacional(
                    "AR7101", "Neuquen", "Cordoba", "23/09/2026", 180, "Cordoba");

            VueloInternacional internacional = new VueloInternacional(
                    "AR7201", "Buenos Aires", "Santiago", "24/09/2026", 220, "Chile", true);

            nacional.reservarPasajero(ana);
            internacional.reservarPasajero(ana);
            internacional.reservarPasajero(bruno);

            nacional.agregarTripulante(piloto);
            nacional.agregarTripulante(tcp);
            internacional.agregarTripulante(piloto);
            internacional.agregarTripulante(tcp);

            pm.makePersistentAll(List.of(ana, bruno, piloto, tcp));
            pm.makePersistentAll(List.of(nacional, internacional));

            tx.commit();
            System.out.println("Se persistieron vuelos, pasajeros, tripulantes y relaciones.");
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            pm.close();
        }
    }

    private static void consultarDesdeVuelo(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            Extent<Vuelo> extent = pm.getExtent(Vuelo.class, true);
            Query<Vuelo> query = pm.newQuery(extent);

            @SuppressWarnings("unchecked")
            Collection<Vuelo> vuelos = (Collection<Vuelo>) query.execute();

            System.out.println("\nRelaciones vistas desde Vuelo:");
            for (Vuelo vuelo : vuelos) {
                System.out.println("\n" + vuelo.getTipo() + " " + vuelo.getNumero());
                System.out.println("  Pasajeros:");
                for (Pasajero p : vuelo.getPasajeros()) {
                    System.out.println("  - " + p.getDni() + " | " + p.getNombreCompleto());
                }
                System.out.println("  Tripulacion:");
                for (Tripulante t : vuelo.getTripulacion()) {
                    System.out.println("  - " + t.getLegajo() + " | " + t.getRol()
                            + " | " + t.getNombreCompleto());
                }
            }

            query.closeAll();
            extent.closeAll();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            pm.close();
        }
    }

    private static void consultarDesdePasajero(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            Query<Pasajero> query = pm.newQuery(Pasajero.class);

            @SuppressWarnings("unchecked")
            Collection<Pasajero> pasajeros = (Collection<Pasajero>) query.execute();

            System.out.println("\nRelacion bidireccional vista desde Pasajero:");
            for (Pasajero p : pasajeros) {
                System.out.println("\n" + p.getNombreCompleto() + " tiene reservados:");
                for (Vuelo vuelo : p.getVuelosReservados()) {
                    System.out.println("  - " + vuelo.getNumero() + " | " + vuelo.getTipo());
                }
            }

            query.closeAll();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            pm.close();
        }
    }
}
