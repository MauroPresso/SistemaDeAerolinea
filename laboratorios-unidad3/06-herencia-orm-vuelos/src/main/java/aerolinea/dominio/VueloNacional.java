package aerolinea.dominio;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(table = "VUELOS_NACIONALES")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public class VueloNacional extends Vuelo {

    @Persistent
    @Column(name = "PROVINCIA_DESTINO", length = 100)
    private String provinciaDestino;

    public VueloNacional() {
        super();
    }

    public VueloNacional(
            String numero,
            String origen,
            String destino,
            String fecha,
            int capacidad,
            String provinciaDestino) {

        super(numero, origen, destino, fecha, capacidad);
        this.provinciaDestino = provinciaDestino;
    }

    public String getProvinciaDestino() {
        return provinciaDestino;
    }

    @Override
    public String getTipo() {
        return "Nacional";
    }

    @Override
    public String toString() {
        return "VueloNacional{"
                + datosComunes()
                + ", provinciaDestino='" + provinciaDestino + '\''
                + '}';
    }
}
