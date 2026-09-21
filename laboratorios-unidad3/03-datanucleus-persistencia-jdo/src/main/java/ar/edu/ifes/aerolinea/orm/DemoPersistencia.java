package ar.edu.ifes.aerolinea.orm;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Demostracion minima del ciclo JDO:
 *
 * 1) crear PersistenceManagerFactory;
 * 2) obtener PersistenceManager;
 * 3) abrir transaccion;
 * 4) persistir objetos;
 * 5) confirmar con commit;
 * 6) consultar los objetos persistidos.
 */
public final class DemoPersistencia {

    private DemoPersistencia() {
    }

    public static void main(String[] args) throws Exception {
        Properties propiedades = cargarPropiedades();

        PersistenceManagerFactory pmf =
                JDOHelper.getPersistenceManagerFactory(propiedades);

        try {
            guardarVuelos(pmf);
            listarVuelos(pmf);
        } finally {
            pmf.close();
        }
    }

    private static Properties cargarPropiedades() throws IOException {
        Properties propiedades = new Properties();

        try (FileInputStream entrada =
                     new FileInputStream("datanucleus.properties")) {
            propiedades.load(entrada);
        }

        return propiedades;
    }

    private static void guardarVuelos(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            pm.makePersistent(
                    new VueloPersistente(
                            "AR1001",
                            "Neuquen",
                            "Buenos Aires"));

            pm.makePersistent(
                    new VueloPersistente(
                            "AR2002",
                            "Buenos Aires",
                            "Bariloche"));

            pm.makePersistent(
                    new VueloPersistente(
                            "AR3003",
                            "Cordoba",
                            "Mendoza"));

            tx.commit();

            System.out.println("Se persistieron 3 vuelos correctamente.");
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
    private static void listarVuelos(PersistenceManagerFactory pmf) {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Query<VueloPersistente> consulta =
                    pm.newQuery(VueloPersistente.class);

            List<VueloPersistente> vuelos =
                    (List<VueloPersistente>) consulta.execute();

            System.out.println();
            System.out.println("Vuelos recuperados desde H2:");

            for (VueloPersistente vuelo : vuelos) {
                System.out.println("- " + vuelo);
            }

            consulta.closeAll();
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
