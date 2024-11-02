package org.demo.parcialmagneto.services;

import org.demo.parcialmagneto.entities.Dna;
import org.demo.parcialmagneto.repositories.DnaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class DnaService {

    // Repositorio para manejar las entidades de ADN
    private final DnaRepository dnaRepository;
    // Longitud mínima de la secuencia para considerarse mutante
    private static final int SEQUENCE_LENGTH = 4;

    // Constructor que inyecta el repositorio de ADN
    @Autowired
    public DnaService(DnaRepository dnaRepository, StatsService statsService) {
        this.dnaRepository = dnaRepository;
    }

   //Metodo principal que determina si un ADN es mutante
    public static boolean isMutant(String[] dna) {
        int n = dna.length;

        // Verificamos si hay más de una secuencia mutante en filas, columnas o diagonales
        long sequenceCount = Stream.of(
                        checkDirection(dna, n, 0, 1),   // Verificar en filas
                        checkDirection(dna, n, 1, 0),   // Verificar en columnas
                        checkDirection(dna, n, 1, 1),   // Verificar en diagonales
                        checkDirection(dna, n, 1, -1)   // Verificar en diagonales
                ).mapToLong(count -> count)  // Conteo total de secuencias encontradas
                .sum();

        // Es mutante si hay más de 1 secuencia mutante
        return sequenceCount > 1;
    }


    // Verifica en una dirección específica (dx, dy) y cuenta las secuencias mutantes.
    private static long checkDirection(String[] dna, int n, int dx, int dy) {
        // Usamos `flatMap` para recorrer toda la matriz y contar todas las secuencias en la dirección (dx, dy)
        return IntStream.range(0, n)
                .flatMap(i -> IntStream.range(0, n)
                        .map(j -> countSequencesInDirection(dna, i, j, dx, dy, n)))
                .sum();  // Sumar todas las secuencias mutantes encontradas en la dirección
    }


     //Cuenta las secuencias de ADN en una dirección específica.
    private static int countSequencesInDirection(String[] dna, int x, int y, int dx, int dy, int n) {
        int count = 0; // Conteo de secuencias mutantes encontradas

        // Verifica si podemos analizar una secuencia completa dentro de los límites
        while (x + (SEQUENCE_LENGTH - 1) * dx < n && y + (SEQUENCE_LENGTH - 1) * dy >= 0 && y + (SEQUENCE_LENGTH - 1) * dy < n) {
            char first = dna[x].charAt(y); // Carácter inicial de la secuencia
            boolean isMutantSequence = true; // Indicador de si la secuencia es mutante

            // Verificamos la secuencia de longitud SEQUENCE_LENGTH
            for (int i = 1; i < SEQUENCE_LENGTH; i++) {
                if (dna[x + i * dx].charAt(y + i * dy) != first) {
                    isMutantSequence = false; // No es una secuencia mutante
                    break; // Salimos del bucle si encontramos una discrepancia
                }
            }

            // Si encontramos una secuencia mutante, la contamos
            if (isMutantSequence) {
                count++;
                // Continuar buscando otras secuencias en la misma dirección desplazando la posición
                x += SEQUENCE_LENGTH * dx; // Salta la longitud de la secuencia
                y += SEQUENCE_LENGTH * dy; // Salta la longitud de la secuencia
            } else {
                // Si no es mutante, avanzamos solo 1 posición
                x += dx; // Avanzamos a la siguiente posición en la dirección x
                y += dy; // Avanzamos a la siguiente posición en la dirección y
            }
        }
        return count; // Retorna el total de secuencias mutantes encontradas
    }


    //Analiza un ADN y determina si es mutante.
    public boolean analyzeDna(String[] dna) {
        String dnaSequence = String.join(",", dna); // Unir el ADN en una sola cadena para la base de datos

        // Verificamos si el ADN ya existe en la base de datos
        Optional<Dna> existingDna = dnaRepository.findByDna(dnaSequence);
        if (existingDna.isPresent()) {
            // Si el ADN ya fue analizado, retornamos el resultado
            return existingDna.get().isMutant();
        }

        // Determinamos si el ADN es mutante y lo guardamos en la base de datos
        boolean isMutant = isMutant(dna);
        Dna dnaEntity = Dna.builder()
                .dna(dnaSequence) // Guardamos la cadena del ADN
                .isMutant(isMutant) // Indicamos si es mutante o no
                .build();
        dnaRepository.save(dnaEntity); // Guardamos el ADN en la base de datos

        return isMutant; // Retornamos si es mutante
    }
}
