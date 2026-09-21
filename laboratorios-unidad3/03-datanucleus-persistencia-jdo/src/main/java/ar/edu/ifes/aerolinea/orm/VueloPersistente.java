package ar.edu.ifes.aerolinea.orm;

/**
 * Entidad minima utilizada para estudiar persistencia ORM con JDO.
 */
public class VueloPersistente {

    private String numero;
    private String origen;
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
        return "VueloPersistente{" +
                "numero='" + numero + '\'' +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                '}';
    }
}
