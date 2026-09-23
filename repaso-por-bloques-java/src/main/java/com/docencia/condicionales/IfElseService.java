package com.docencia.condicionales;
/** Servicio para practicar if/else. */
public interface IfElseService {
    /**
     * Interfaz que clasifica la edad
     * @param edad entero
     * @return valor de la edad
     */
    String clasificarEdad(Integer edad);
    String evaluarNota(Integer nota);
    Boolean puedeAcceder(Boolean activo, Integer edad);
    String clasificarTemperatura(Double temperatura);
    String calcularResultadoComparacion(Integer primero, Integer segundo);
}
