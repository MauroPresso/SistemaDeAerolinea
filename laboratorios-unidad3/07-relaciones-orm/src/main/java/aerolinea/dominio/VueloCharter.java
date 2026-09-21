package aerolinea.dominio;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(table = "VUELOS_CHARTER")
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
public class VueloCharter extends Vuelo {

    @Persistent
    @Column(name = "EMPRESA_CONTRATANTE", length = 150)
    private String empresaContratante;

    @Persistent
    @Column(name = "COSTO_TOTAL")
    private double costoTotal;

    public VueloCharter() {
        super();
    }

    public VueloCharter(
            String numero,
            String origen,
            String destino,
            String fecha,
            int capacidad,
            String empresaContratante,
            double costoTotal) {

        super(numero, origen, destino, fecha, capacidad);
        this.empresaContratante = empresaContratante;
        this.costoTotal = costoTotal;
    }

    public String getEmpresaContratante() {
        return empresaContratante;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    @Override
    public String getTipo() {
        return "Charter";
    }

    @Override
    public String toString() {
        return "VueloCharter{"
                + datosComunes()
                + ", empresaContratante='" + empresaContratante + '\''
                + ", costoTotal=" + costoTotal
                + '}';
    }
}
