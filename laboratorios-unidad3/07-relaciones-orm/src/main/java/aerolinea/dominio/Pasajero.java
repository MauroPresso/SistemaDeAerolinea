package aerolinea.dominio;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@PersistenceCapable(table = "PASAJEROS")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public class Pasajero extends Persona {

    @Persistent
    @Column(name = "NUMERO_PASAPORTE", length = 50)
    private String numeroPasaporte;

    @Persistent(mappedBy = "pasajeros")
    private Set<Vuelo> vuelosReservados = new LinkedHashSet<>();

    public Pasajero() {
        super();
    }

    public Pasajero(int dni, String nombre, String apellido, String numeroPasaporte) {
        super(dni, nombre, apellido);
        this.numeroPasaporte = numeroPasaporte == null ? "" : numeroPasaporte;
    }

    public String getNumeroPasaporte() {
        return numeroPasaporte;
    }

    public Set<Vuelo> getVuelosReservados() {
        return Collections.unmodifiableSet(vuelosReservados);
    }

    void agregarVueloReservado(Vuelo vuelo) {
        vuelosReservados.add(vuelo);
    }

    void quitarVueloReservado(Vuelo vuelo) {
        vuelosReservados.remove(vuelo);
    }
}
