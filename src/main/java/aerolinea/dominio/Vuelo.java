package aerolinea.dominio;

import aerolinea.excepcion.VueloNoDisponibleException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Clase abstracta base para todos los tipos de vuelo.
 *
 * <p>Unidad 3: la jerarquia de vuelos se persiste con DataNucleus/JDO usando
 * estrategia NEW_TABLE.</p>
 */
@PersistenceCapable(
        table = "VUELOS",
        identityType = IdentityType.APPLICATION,
        detachable = "true")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public abstract class Vuelo
        implements IOperable, Comparable<Vuelo>, Serializable {

    private static final long serialVersionUID = 1L;

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

    /**
     * Relacion muchos-a-muchos bidireccional Vuelo <-> Pasajero.
     */
    @Persistent(table = "VUELO_PASAJERO")
    @Join(column = "VUELO_NUMERO")
    @Element(column = "PASAJERO_DNI")
    @Order(extensions = @Extension(
            vendorName = "datanucleus",
            key = "list-ordering",
            value = "dni ASC"))
    private ArrayList<Pasajero> pasajeros = new ArrayList<>();

    /**
     * Relacion unidireccional Vuelo -> Tripulante.
     */
    @Persistent(table = "VUELO_TRIPULANTE")
    @Join(column = "VUELO_NUMERO")
    @Element(column = "TRIPULANTE_DNI")
    @Order(column = "ORDEN_TRIPULANTE")
    private ArrayList<Tripulante> tripulacion = new ArrayList<>();

    /**
     * Constructor protegido para DataNucleus.
     */
    protected Vuelo() {
        this.estado = EstadoVuelo.PROGRAMADO;
    }

    public Vuelo(
            String numero,
            String origen,
            String destino,
            String fecha,
            int capacidad) {

        setNumero(numero);
        setOrigen(origen);
        setDestino(destino);
        setFecha(fecha);
        setCapacidad(capacidad);
        this.estado = EstadoVuelo.PROGRAMADO;
    }

    public abstract String getTipo();

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero =
                validarTextoObligatorio(numero, "numero");
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen =
                validarTextoObligatorio(origen, "origen");
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino =
                validarTextoObligatorio(destino, "destino");
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha =
                validarTextoObligatorio(fecha, "fecha");
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor que cero.");
        }

        if (pasajeros != null
                && capacidad < pasajeros.size()) {

            throw new IllegalArgumentException(
                    "La capacidad no puede ser menor "
                            + "a los asientos ya ocupados.");
        }

        this.capacidad = capacidad;
    }

    public EstadoVuelo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVuelo estado) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado no puede ser nulo.");
        }
        this.estado = estado;
    }

    public List<Pasajero> getPasajeros() {
        return Collections.unmodifiableList(pasajeros);
    }

    public List<Tripulante> getTripulacion() {
        return Collections.unmodifiableList(tripulacion);
    }

    public void reservarPasajero(Pasajero pasajero)
            throws VueloNoDisponibleException {

        if (pasajero == null) {
            throw new IllegalArgumentException(
                    "El pasajero no puede ser nulo.");
        }

        if (pasajeros.contains(pasajero)) {
            return;
        }

        validarDisponibilidadParaReserva();

        pasajeros.add(pasajero);
        pasajero.agregarVueloReservado(this);
    }

    public boolean cancelarReserva(Pasajero pasajero) {
        if (pasajero == null) {
            throw new IllegalArgumentException(
                    "El pasajero no puede ser nulo.");
        }

        boolean eliminado =
                pasajeros.remove(pasajero);

        if (eliminado) {
            pasajero.quitarVueloReservado(this);
        }

        return eliminado;
    }

    public void agregarTripulante(Tripulante tripulante) {
        if (tripulante == null) {
            throw new IllegalArgumentException(
                    "El tripulante no puede ser nulo.");
        }

        if (!tripulacion.contains(tripulante)) {
            tripulacion.add(tripulante);
        }
    }

    public int getAsientosOcupados() {
        return pasajeros.size();
    }

    public int getAsientosDisponibles() {
        return capacidad - pasajeros.size();
    }

    public boolean hayAsientosDisponibles() {
        return getAsientosDisponibles() > 0;
    }

    @Override
    public void embarcar() {
        if (estado == EstadoVuelo.PROGRAMADO) {
            estado = EstadoVuelo.EN_VUELO;

            System.out.println(
                    "Embarque iniciado para el vuelo "
                            + numero + ".");
        } else {
            System.out.println(
                    "No se puede embarcar el vuelo "
                            + numero
                            + " porque esta "
                            + estado
                            + ".");
        }
    }

    @Override
    public void cancelar() {
        estado = EstadoVuelo.CANCELADO;

        System.out.println(
                "Vuelo " + numero + " cancelado.");
    }

    public void mostrarInfo() {
        System.out.println(
                "Vuelo " + numero
                        + " | Tipo: " + getTipo()
                        + " | Origen: " + origen
                        + " | Destino: " + destino
                        + " | Fecha: " + fecha
                        + " | Estado: " + estado
                        + " | Ocupados: "
                        + getAsientosOcupados()
                        + "/" + capacidad
                        + " | Disponibles: "
                        + getAsientosDisponibles());

        String detalle = obtenerDetalleAdicional();

        if (!detalle.isEmpty()) {
            System.out.println("Detalle: " + detalle);
        }
    }

    protected String obtenerDetalleAdicional() {
        return "";
    }

    private void validarDisponibilidadParaReserva()
            throws VueloNoDisponibleException {

        if (estado == EstadoVuelo.EN_VUELO) {
            throw new VueloNoDisponibleException(
                    "El vuelo " + numero
                            + " ya esta en vuelo.");
        }

        if (estado == EstadoVuelo.CANCELADO) {
            throw new VueloNoDisponibleException(
                    "El vuelo " + numero
                            + " esta cancelado.");
        }

        if (!hayAsientosDisponibles()) {
            throw new VueloNoDisponibleException(
                    "El vuelo " + numero
                            + " no tiene asientos disponibles.");
        }
    }

    private String validarTextoObligatorio(
            String valor,
            String campo) {

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El campo " + campo
                            + " no puede estar vacio.");
        }

        return valor.trim();
    }

    @Override
    public int compareTo(Vuelo otro) {
        return this.numero.compareToIgnoreCase(
                otro.numero);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Vuelo)) {
            return false;
        }

        Vuelo otro = (Vuelo) obj;

        return numero.equalsIgnoreCase(
                otro.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero.toUpperCase());
    }
}
