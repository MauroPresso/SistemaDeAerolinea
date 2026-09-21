package ar.edu.ifes.aerolinea.orm;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * Inspector didactico.
 *
 * No forma parte del repositorio ORM. Solo usa metadata JDBC para mostrar
 * las tablas y columnas que SchemaTool creo en H2.
 */
public final class VerSchema {

    private static final String URL = "jdbc:h2:file:./data/aerolinea_orm";
    private static final String USUARIO = "sa";
    private static final String CLAVE = "";

    private VerSchema() {
    }

    public static void main(String[] args) throws Exception {
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CLAVE)) {
            DatabaseMetaData metaData = conexion.getMetaData();

            System.out.println("Base de datos: " + metaData.getDatabaseProductName());
            System.out.println("Tablas creadas por SchemaTool:");

            try (ResultSet tablas = metaData.getTables(null, null, "VUELOS", new String[]{"TABLE"})) {
                boolean encontrada = false;

                while (tablas.next()) {
                    encontrada = true;
                    String tabla = tablas.getString("TABLE_NAME");
                    System.out.println("- " + tabla);

                    try (ResultSet columnas = metaData.getColumns(null, null, tabla, null)) {
                        while (columnas.next()) {
                            System.out.println(
                                    "    " + columnas.getString("COLUMN_NAME")
                                            + " : " + columnas.getString("TYPE_NAME"));
                        }
                    }
                }

                if (!encontrada) {
                    System.out.println("No se encontro la tabla VUELOS.");
                    System.out.println("Ejecute primero: mvn datanucleus:schema-create");
                }
            }
        }
    }
}
