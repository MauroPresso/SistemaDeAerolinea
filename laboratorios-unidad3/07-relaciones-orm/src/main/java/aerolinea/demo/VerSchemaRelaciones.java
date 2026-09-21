package aerolinea.demo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

public final class VerSchemaRelaciones {

    private static final String URL = "jdbc:h2:file:./data/aerolinea_relaciones";

    private static final String[] TABLAS = {
            "VUELOS", "VUELOS_NACIONALES", "VUELOS_INTERNACIONALES", "VUELOS_CHARTER",
            "PERSONAS", "PASAJEROS", "TRIPULANTES",
            "VUELO_PASAJERO", "VUELO_TRIPULANTE"
    };

    private VerSchemaRelaciones() {
    }

    public static void main(String[] args) throws Exception {
        try (Connection conexion = DriverManager.getConnection(URL, "sa", "")) {
            DatabaseMetaData metaData = conexion.getMetaData();
            System.out.println("Base de datos: " + metaData.getDatabaseProductName());

            for (String tabla : TABLAS) {
                try (ResultSet rs = metaData.getTables(null, null, tabla, new String[]{"TABLE"})) {
                    if (!rs.next()) {
                        System.out.println("\nNo se encontro: " + tabla);
                        continue;
                    }
                }

                System.out.println("\nTabla: " + tabla);
                try (ResultSet cols = metaData.getColumns(null, null, tabla, null)) {
                    while (cols.next()) {
                        System.out.println("- " + cols.getString("COLUMN_NAME")
                                + " : " + cols.getString("TYPE_NAME"));
                    }
                }
            }
        }
    }
}
