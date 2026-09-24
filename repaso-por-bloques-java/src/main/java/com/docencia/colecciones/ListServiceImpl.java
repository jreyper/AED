package com.docencia.colecciones;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ListServiceImpl implements ListService {

    @Override
    public List<String> filtrarPalabrasPorLongitud(List<String> palabras, Integer longitudMinima) {
        List<String> out = new ArrayList<>();
        if (palabras == null || palabras.isEmpty() || longitudMinima == null || longitudMinima <= 0) {
            throw new IllegalArgumentException();
        }
        for (String palabra : palabras) {
            if (palabra.length() >= longitudMinima) {
                out.add(palabra);
            }
        }
        return out;

    }

    @Override
    public List<Integer> ordenarNumerosAscendente(List<Integer> numeros) {
        List<Integer> resultado = new ArrayList<>(numeros);
        if (numeros == null || numeros.isEmpty()){
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < resultado.size(); i++) {
            for (int j = i + 1; j < resultado.size(); j++) {
                if (resultado.get(i) > resultado.get(j)) {
                    int temporal = resultado.get(i);
                    resultado.set(i, resultado.get(j));
                    resultado.set(j, temporal);
                }
            }
        }

        return resultado;
    }

    @Override
    public Integer sumarElementosLista(List<Integer> numeros) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sumarElementosLista'");
    }

    @Override
    public Double calcularMediaLista(List<Integer> numeros) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calcularMediaLista'");
    }

    @Override
    public List<Integer> eliminarNumerosDuplicados(List<Integer> numeros) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminarNumerosDuplicados'");
    }

}
