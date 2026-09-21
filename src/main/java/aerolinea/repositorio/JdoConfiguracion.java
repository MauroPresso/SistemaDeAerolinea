package aerolinea.repositorio;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * Fabrica de PersistenceManagerFactory a partir de datanucleus.properties.
 */
final class JdoConfiguracion {

    private static final String RECURSO =
            "/datanucleus.properties";

    private JdoConfiguracion() {
    }

    static PersistenceManagerFactory crearFactory()
            throws IOException {

        Properties propiedades =
                new Properties();

        try (InputStream entrada =
                     JdoConfiguracion.class
                             .getResourceAsStream(RECURSO)) {

            if (entrada == null) {
                throw new IOException(
                        "No se encontro "
                                + RECURSO
                                + " en el classpath.");
            }

            propiedades.load(entrada);
        }

        return JDOHelper
                .getPersistenceManagerFactory(
                        propiedades);
    }
}
