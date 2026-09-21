package ar.edu.ifes.aerolinea.orm;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * Inspector didactico para comprobar la tabla creada desde anotaciones.
 */
public final class VerSchemaAnotaciones {

    private static final String URL =
            "jdbc:h2:file:./data/aerolinea_anotaciones";

    private VerSchemaAnotaciones() {
    }

    public static void main(String[] args) throws Exception {
        try (Connection conexion =
                     DriverManager.getConnection(URL, "sa", "")) {

            DatabaseMetaData metaData = conexion.getMetaData();

            System.out.println("Base de datos: " +
                    metaData.getDatabaseProductName());

            try (ResultSet tablas = metaData.getTables(
                    null,
                    null,
                    "VUELOS_ANOTACIONES",
                    new String[]{"TABLE"})) {

                if (!tablas.next()) {
                    System.out.println(
                            "No se encontro VUELOS_ANOTACIONES.");
                    return;
                }

                System.out.println("Tabla: VUELOS_ANOTACIONES");

                try (ResultSet columnas = metaData.getColumns(
                        null,
                        null,
                        "VUELOS_ANOTACIONES",
                        null)) {

                    while (columnas.next()) {
                        System.out.println(
                                "- " + columnas.getString("COLUMN_NAME")
                                + " : "
                                + columnas.getString("TYPE_NAME"));
                    }
                }
            }
        }
    }
}
