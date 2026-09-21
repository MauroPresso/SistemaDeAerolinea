package aerolinea.demo;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Demuestra persistencia polimorfica de la jerarquia Vuelo.
 */
public final class DemoHerenciaOrm {

    private DemoHerenciaOrm() {
    }

    public static void main(String[] args) throws Exception {
        Properties propiedades = new Properties();

        try (FileInputStream entrada =
                     new FileInputStream("datanucleus.properties")) {
            propiedades.load(entrada);
        }

        PersistenceManagerFactory pmf =
                JDOHelper.getPersistenceManagerFactory(propiedades);

        try {
            limpiarDatos(pmf);
            persistirVuelos(pmf);
            consultarPolimorficamente(pmf);
        } finally {
            pmf.close();
        }
    }

    private static void limpiarDatos(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Extent<Vuelo> extent = pm.getExtent(Vuelo.class, true);
            Query<Vuelo> query = pm.newQuery(extent);

            @SuppressWarnings("unchecked")
            Collection<Vuelo> existentes =
                    (Collection<Vuelo>) query.execute();

            List<Vuelo> copia = new ArrayList<>(existentes);
            query.closeAll();
            extent.closeAll();

            if (!copia.isEmpty()) {
                pm.deletePersistentAll(copia);
            }

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            pm.close();
        }
    }

    private static void persistirVuelos(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            pm.makePersistent(
                    new VueloNacional(
                            "AR6101",
                            "Neuquen",
                            "Cordoba",
                            "20/09/2026",
                            180,
                            "Cordoba"));

            pm.makePersistent(
                    new VueloInternacional(
                            "AR6201",
                            "Buenos Aires",
                            "Santiago",
                            "21/09/2026",
                            220,
                            "Chile",
                            true));

            pm.makePersistent(
                    new VueloCharter(
                            "AR6301",
                            "Neuquen",
                            "Comodoro Rivadavia",
                            "22/09/2026",
                            90,
                            "Energia Patagonia SA",
                            18500000.0));

            tx.commit();

            System.out.println(
                    "Se persistieron 3 vuelos de subtipos diferentes.");
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            pm.close();
        }
    }

    private static void consultarPolimorficamente(
            PersistenceManagerFactory pmf) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            /*
             * true indica que el Extent de Vuelo incluye sus subclases.
             */
            Extent<Vuelo> extent = pm.getExtent(Vuelo.class, true);
            Query<Vuelo> query = pm.newQuery(extent);

            @SuppressWarnings("unchecked")
            Collection<Vuelo> vuelos =
                    (Collection<Vuelo>) query.execute();

            System.out.println();
            System.out.println("Consulta desde la clase abstracta Vuelo:");

            for (Vuelo vuelo : vuelos) {
                System.out.println(
                        "- " + vuelo.getTipo()
                                + " | "
                                + vuelo);
            }

            query.closeAll();
            extent.closeAll();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            pm.close();
        }
    }
}
