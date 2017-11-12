package userservice.Model;

import java.sql.Timestamp;

/**
 *
 * @author Horacio
 */
public class Estrategia {

    String nombre;
    String sentencia;
    Boolean estado;
    int frecuencia;
    int dias;
    int hora_inicio;
    int hora_final;
    Timestamp proxima_ejecucion;

    public Estrategia(String nombre, String sentencia, Boolean estado, int frecuencia, int dias, int hora_inicio, int hora_final, Timestamp proxima_ejecucion) {
        this.nombre = nombre;
        this.sentencia = sentencia;
        this.estado = estado;
        this.frecuencia = frecuencia;
        this.dias = dias;
        this.hora_inicio = hora_inicio;
        this.hora_final = hora_final;
        this.proxima_ejecucion = proxima_ejecucion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSentencia() {
        return sentencia;
    }

    public void setSentencia(String sentencia) {
        this.sentencia = sentencia;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public int getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(int hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public int getHora_final() {
        return hora_final;
    }

    public void setHora_final(int hora_final) {
        this.hora_final = hora_final;
    }

    public Timestamp getProxima_ejecucion() {
        return proxima_ejecucion;
    }

    public void setProxima_ejecucion(Timestamp proxima_ejecucion) {
        this.proxima_ejecucion = proxima_ejecucion;
    }
}
