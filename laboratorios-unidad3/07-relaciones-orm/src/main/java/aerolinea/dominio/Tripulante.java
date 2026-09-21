package aerolinea.dominio;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(table = "TRIPULANTES")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public class Tripulante extends Persona {

    @Persistent
    @Column(name = "LEGAJO")
    private int legajo;

    @Persistent
    @Column(name = "ROL", length = 100)
    private String rol;

    public Tripulante() {
        super();
    }

    public Tripulante(int dni, String nombre, String apellido, int legajo, String rol) {
        super(dni, nombre, apellido);
        this.legajo = legajo;
        this.rol = rol;
    }

    public int getLegajo() {
        return legajo;
    }

    public String getRol() {
        return rol;
    }
}
