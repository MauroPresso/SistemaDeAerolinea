package ar.edu.ifes.aerolinea.orm;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

/**
 * Demuestra que el flujo JDO es el mismo que en la etapa XML.
 * Lo unico que cambia es donde se declara la metadata ORM.
 */
public final class DemoAnotaciones {

    private DemoAnotaciones() {
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
            guardar(pmf);
            consultar(pmf);
        } finally {
            pmf.close();
        }
    }

    private static void guardar(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            pm.makePersistent(
                    new VueloAnotado(
                            "AU410",
                            "Neuquen",
                            "Cordoba"));

            pm.makePersistent(
                    new VueloAnotado(
                            "AU411",
                            "Cordoba",
                            "Salta"));

            tx.commit();

            System.out.println("Se persistieron 2 vuelos usando metadata por anotaciones.");
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            pm.close();
        }
    }

    @SuppressWarnings("unchecked")
    private static void consultar(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Query<VueloAnotado> query =
                    pm.newQuery(VueloAnotado.class);

            List<VueloAnotado> vuelos =
                    (List<VueloAnotado>) query.execute();

            System.out.println();
            System.out.println("Vuelos recuperados con JDO:");

            for (VueloAnotado vuelo : vuelos) {
                System.out.println("- " + vuelo);
            }

            query.closeAll();
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
