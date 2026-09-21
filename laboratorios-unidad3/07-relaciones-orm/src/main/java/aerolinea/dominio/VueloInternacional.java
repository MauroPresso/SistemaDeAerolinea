package aerolinea.dominio;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(table = "VUELOS_INTERNACIONALES")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public class VueloInternacional extends Vuelo {

    @Persistent
    @Column(name = "PAIS_DESTINO", length = 100)
    private String paisDestino;

    @Persistent
    @Column(name = "REQUIERE_PASAPORTE")
    private boolean requierePasaporte;

    public VueloInternacional() {
        super();
    }

    public VueloInternacional(
            String numero,
            String origen,
            String destino,
            String fecha,
            int capacidad,
            String paisDestino,
            boolean requierePasaporte) {

        super(numero, origen, destino, fecha, capacidad);
        this.paisDestino = paisDestino;
        this.requierePasaporte = requierePasaporte;
    }

    public String getPaisDestino() {
        return paisDestino;
    }

    public boolean isRequierePasaporte() {
        return requierePasaporte;
    }

    @Override
    public String getTipo() {
        return "Internacional";
    }

    @Override
    public String toString() {
        return "VueloInternacional{"
                + datosComunes()
                + ", paisDestino='" + paisDestino + '\''
                + ", requierePasaporte=" + requierePasaporte
                + '}';
    }
}
