package aerolinea.orm;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(
        table = "VUELOS_REPOSITORIO",
        identityType = IdentityType.APPLICATION,
        detachable = "true")
public class VueloPersistente {

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

    public VueloPersistente() {
    }

    public VueloPersistente(String numero, String origen, String destino) {
        this.numero = numero;
        this.origen = origen;
        this.destino = destino;
    }

    public String getNumero() {
        return numero;
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
        return "VueloPersistente{" +
                "numero='" + numero + '\'' +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                '}';
    }
}
