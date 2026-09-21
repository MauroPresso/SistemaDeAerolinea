package aerolinea.dominio;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "PERSONAS", identityType = IdentityType.APPLICATION, detachable = "true")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public abstract class Persona {

    @PrimaryKey
    @Persistent
    @Column(name = "DNI")
    private int dni;

    @Persistent
    @Column(name = "NOMBRE", length = 100)
    private String nombre;

    @Persistent
    @Column(name = "APELLIDO", length = 100)
    private String apellido;

    protected Persona() {
    }

    protected Persona(int dni, String nombre, String apellido) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public int getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombreCompleto() {
        return apellido + ", " + nombre;
    }
}
