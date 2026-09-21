package aerolinea.repositorio;

import aerolinea.orm.VueloPersistente;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class RepositorioJdoVuelo
        implements IRepositorio<VueloPersistente>, AutoCloseable {

    private final PersistenceManagerFactory pmf;

    public RepositorioJdoVuelo(String archivoPropiedades) throws IOException {
        Properties propiedades = new Properties();

        try (FileInputStream entrada =
                     new FileInputStream(archivoPropiedades)) {
            propiedades.load(entrada);
        }

        this.pmf = JDOHelper.getPersistenceManagerFactory(propiedades);
    }

    @Override
    public void guardar(List<VueloPersistente> elementos) throws IOException {
        if (elementos == null) {
            throw new IllegalArgumentException("La lista no puede ser nula.");
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Query<VueloPersistente> consulta =
                    pm.newQuery(VueloPersistente.class);

            @SuppressWarnings("unchecked")
            List<VueloPersistente> resultadoConsulta =
                    (List<VueloPersistente>) consulta.execute();

            List<VueloPersistente> existentes =
                    new ArrayList<>(resultadoConsulta);

            consulta.closeAll();

            Map<String, VueloPersistente> existentesPorNumero =
                    new LinkedHashMap<>();

            for (VueloPersistente vuelo : existentes) {
                existentesPorNumero.put(vuelo.getNumero(), vuelo);
            }

            for (VueloPersistente recibido : elementos) {
                VueloPersistente existente =
                        existentesPorNumero.remove(recibido.getNumero());

                if (existente == null) {
                    pm.makePersistent(
                            new VueloPersistente(
                                    recibido.getNumero(),
                                    recibido.getOrigen(),
                                    recibido.getDestino()));
                } else {
                    existente.setOrigen(recibido.getOrigen());
                    existente.setDestino(recibido.getDestino());
                }
            }

            if (!existentesPorNumero.isEmpty()) {
                pm.deletePersistentAll(existentesPorNumero.values());
            }

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new IOException("No se pudo guardar mediante JDO.", e);
        } finally {
            pm.close();
        }
    }

    @Override
    public List<VueloPersistente> consultar() throws IOException {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Query<VueloPersistente> consulta =
                    pm.newQuery(VueloPersistente.class);

            @SuppressWarnings("unchecked")
            List<VueloPersistente> persistentes =
                    (List<VueloPersistente>) consulta.execute();

            List<VueloPersistente> resultado = new ArrayList<>();

            for (VueloPersistente vuelo : persistentes) {
                resultado.add(
                        new VueloPersistente(
                                vuelo.getNumero(),
                                vuelo.getOrigen(),
                                vuelo.getDestino()));
            }

            consulta.closeAll();
            tx.commit();

            return resultado;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new IOException("No se pudo consultar mediante JDO.", e);
        } finally {
            pm.close();
        }
    }

    @Override
    public void close() {
        if (!pmf.isClosed()) {
            pmf.close();
        }
    }
}
