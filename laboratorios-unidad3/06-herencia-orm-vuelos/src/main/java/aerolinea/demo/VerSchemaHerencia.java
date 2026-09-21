package aerolinea.demo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * Muestra las tablas creadas para la estrategia NEW_TABLE.
 */
public final class VerSchemaHerencia {

    private static final String URL =
            "jdbc:h2:file:./data/aerolinea_herencia";

    private static final String[] TABLAS = {
            "VUELOS",
            "VUELOS_NACIONALES",
            "VUELOS_INTERNACIONALES",
            "VUELOS_CHARTER"
    };

    private VerSchemaHerencia() {
    }

    public static void main(String[] args) throws Exception {
        try (Connection conexion =
                     DriverManager.getConnection(URL, "sa", "")) {

            DatabaseMetaData metaData = conexion.getMetaData();

            System.out.println(
                    "Base de datos: "
                            + metaData.getDatabaseProductName());

            for (String tabla : TABLAS) {
                mostrarTabla(metaData, tabla);
            }
        }
    }

    private static void mostrarTabla(
            DatabaseMetaData metaData,
            String tabla) throws Exception {

        try (ResultSet tablas =
                     metaData.getTables(
                             null,
                             null,
                             tabla,
                             new String[]{"TABLE"})) {

            if (!tablas.next()) {
                System.out.println();
                System.out.println(
                        "No se encontro la tabla " + tabla);
                return;
            }
        }

        System.out.println();
        System.out.println("Tabla: " + tabla);

        try (ResultSet columnas =
                     metaData.getColumns(
                             null,
                             null,
                             tabla,
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
