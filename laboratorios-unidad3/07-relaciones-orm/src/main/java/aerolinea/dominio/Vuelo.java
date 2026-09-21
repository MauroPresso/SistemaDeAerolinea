package aerolinea.dominio;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@PersistenceCapable(table = "VUELOS", identityType = IdentityType.APPLICATION, detachable = "true")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public abstract class Vuelo {

    @PrimaryKey
    @Persistent
    @Column(name = "NUMERO", length = 30)
    private String numero;

    @Persistent @Column(name = "ORIGEN", length = 100)
    private String origen;

    @Persistent @Column(name = "DESTINO", length = 100)
    private String destino;

    @Persistent @Column(name = "FECHA", length = 30)
    private String fecha;

    @Persistent @Column(name = "CAPACIDAD")
    private int capacidad;

    @Persistent @Column(name = "ESTADO")
    private EstadoVuelo estado;

    @Persistent(table = "VUELO_PASAJERO")
    @Join(column = "VUELO_NUMERO")
    @Element(column = "PASAJERO_DNI")
    private Set<Pasajero> pasajeros = new LinkedHashSet<>();

    @Persistent(table = "VUELO_TRIPULANTE")
    @Join(column = "VUELO_NUMERO")
    @Element(column = "TRIPULANTE_DNI")
    private Set<Tripulante> tripulacion = new LinkedHashSet<>();

    protected Vuelo() {
        estado = EstadoVuelo.PROGRAMADO;
    }

    protected Vuelo(String numero, String origen, String destino, String fecha, int capacidad) {
        this.numero = numero;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        this.capacidad = capacidad;
        this.estado = EstadoVuelo.PROGRAMADO;
    }

    public abstract String getTipo();

    public String getNumero() { return numero; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public String getFecha() { return fecha; }
    public int getCapacidad() { return capacidad; }
    public EstadoVuelo getEstado() { return estado; }

    public Set<Pasajero> getPasajeros() {
        return Collections.unmodifiableSet(pasajeros);
    }

    public Set<Tripulante> getTripulacion() {
        return Collections.unmodifiableSet(tripulacion);
    }

    public void reservarPasajero(Pasajero pasajero) {
        if (pasajeros.add(pasajero)) {
            pasajero.agregarVueloReservado(this);
        }
    }

    public void cancelarReserva(Pasajero pasajero) {
        if (pasajeros.remove(pasajero)) {
            pasajero.quitarVueloReservado(this);
        }
    }

    public void agregarTripulante(Tripulante tripulante) {
        tripulacion.add(tripulante);
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
