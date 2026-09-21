package ar.edu.ifes.aerolinea.orm;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * La misma idea de VueloPersistente, pero sin archivo package.jdo.
 *
 * Toda la metadata ORM se declara mediante anotaciones JDO.
 */
@PersistenceCapable(
        table = "VUELOS_ANOTACIONES",
        identityType = IdentityType.DATASTORE)
@DatastoreIdentity(
        column = "VUELO_ID",
        strategy = IdGeneratorStrategy.IDENTITY)
public class VueloAnotado {

    @Persistent
    @Column(name = "NUMERO")
    private String numero;

    @Persistent
    @Column(name = "ORIGEN")
    private String origen;

    @Persistent
    @Column(name = "DESTINO")
    private String destino;

    public VueloAnotado() {
    }

    public VueloAnotado(String numero, String origen, String destino) {
        this.numero = numero;
        this.origen = origen;
        this.destino = destino;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    @Override
    public String toString() {
        return "VueloAnotado{" +
                "numero='" + numero + '\'' +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                '}';
    }
}
