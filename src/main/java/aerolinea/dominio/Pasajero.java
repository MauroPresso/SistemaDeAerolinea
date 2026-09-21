package aerolinea.dominio;

import aerolinea.excepcion.VueloNoDisponibleException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Representa a una persona que puede reservar vuelos.
 */
@PersistenceCapable(table = "PASAJEROS")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public class Pasajero extends Persona {

    private static final long serialVersionUID = 1L;

    @Persistent
    @Column(name = "NUMERO_PASAPORTE", length = 50)
    private String numeroPasaporte;

    /**
     * Lado inverso de la relacion muchos-a-muchos con Vuelo.
     *
     * <p>Se mantiene ArrayList para conservar la API y la compatibilidad con
     * los archivos .dat anteriores. La lista es "ordered" por numero de vuelo,
     * evitando una segunda columna de indice en una relacion M-N bidireccional.</p>
     */
    @Persistent(mappedBy = "pasajeros")
    @Order(extensions = @Extension(
            vendorName = "datanucleus",
            key = "list-ordering",
            value = "numero ASC"))
    private ArrayList<Vuelo> vuelosReservados = new ArrayList<>();

    protected Pasajero() {
        super();
    }

    public Pasajero(
            int dni,
            String nombre,
            String apellido,
            String numeroPasaporte) {

        super(dni, nombre, apellido);
        this.numeroPasaporte =
                normalizarTextoOpcional(numeroPasaporte);
    }

    public String getNumeroPasaporte() {
        return numeroPasaporte;
    }

    public void setNumeroPasaporte(String numeroPasaporte) {
        this.numeroPasaporte =
                normalizarTextoOpcional(numeroPasaporte);
    }

    public List<Vuelo> getVuelosReservados() {
        return Collections.unmodifiableList(vuelosReservados);
    }

    public void reservarVuelo(Vuelo vuelo)
            throws VueloNoDisponibleException {

        if (vuelo == null) {
            throw new IllegalArgumentException(
                    "El vuelo no puede ser nulo.");
        }

        vuelo.reservarPasajero(this);
    }

    public void cancelarReserva(Vuelo vuelo) {
        if (vuelo == null) {
            throw new IllegalArgumentException(
                    "El vuelo no puede ser nulo.");
        }

        vuelo.cancelarReserva(this);
    }

    public boolean tieneVueloReservado(Vuelo vuelo) {
        return vuelosReservados.contains(vuelo);
    }

    public boolean tieneReservaActiva() {
        return vuelosReservados.stream()
                .anyMatch(vuelo ->
                        vuelo.getEstado()
                                != EstadoVuelo.CANCELADO);
    }

    public void mostrarReservas() {
        System.out.println(
                "Reservas de " + getNombreCompleto() + ":");

        if (vuelosReservados.isEmpty()) {
            System.out.println("No registra reservas.");
            return;
        }

        vuelosReservados.forEach(Vuelo::mostrarInfo);
    }

    void agregarVueloReservado(Vuelo vuelo) {
        if (!vuelosReservados.contains(vuelo)) {
            vuelosReservados.add(vuelo);
        }
    }

    void quitarVueloReservado(Vuelo vuelo) {
        vuelosReservados.remove(vuelo);
    }

    @Override
    public void mostrarInfo() {
        System.out.println(
                "Pasajero: " + getNombreCompleto()
                        + " | DNI: " + getDni()
                        + " | Pasaporte: "
                        + (numeroPasaporte.isEmpty()
                        ? "No informado"
                        : numeroPasaporte)
                        + " | Reservas: "
                        + vuelosReservados.size());
    }

    private String normalizarTextoOpcional(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
