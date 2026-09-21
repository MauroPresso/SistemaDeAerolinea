package aerolinea.dominio;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Copia controlada de la raiz real de la jerarquia de vuelos.
 *
 * En esta etapa se excluyen pasajeros, tripulacion y reservas para estudiar
 * solamente herencia ORM.
 */
@PersistenceCapable(
        table = "VUELOS",
        identityType = IdentityType.APPLICATION,
        detachable = "true")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public abstract class Vuelo {

    @PrimaryKey
    @Persistent
    @Column(name = "NUMERO", length = 30)
    private String numero;

    @Persistent
    @Column(name = "ORIGEN", length = 100)
    private String origen;

    @Persistent
    @Column(name = "DESTINO", length = 100)
    private String destino;

    @Persistent
    @Column(name = "FECHA", length = 30)
    private String fecha;

    @Persistent
    @Column(name = "CAPACIDAD")
    private int capacidad;

    @Persistent
    @Column(name = "ESTADO")
    private EstadoVuelo estado;

    protected Vuelo() {
        this.estado = EstadoVuelo.PROGRAMADO;
    }

    protected Vuelo(
            String numero,
            String origen,
            String destino,
            String fecha,
            int capacidad) {

        this.numero = numero;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        this.capacidad = capacidad;
        this.estado = EstadoVuelo.PROGRAMADO;
    }

    public abstract String getTipo();

    public String getNumero() {
        return numero;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public String getFecha() {
        return fecha;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public EstadoVuelo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVuelo estado) {
        this.estado = estado;
    }

    protected String datosComunes() {
        return "numero='" + numero + '\''
                + ", origen='" + origen + '\''
                + ", destino='" + destino + '\''
                + ", fecha='" + fecha + '\''
                + ", capacidad=" + capacidad
                + ", estado=" + estado;
    }
}
