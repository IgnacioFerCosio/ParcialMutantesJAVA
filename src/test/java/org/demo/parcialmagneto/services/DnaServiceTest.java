package org.demo.parcialmagneto.services;
import org.demo.parcialmagneto.dto.StatsResponse;
import org.demo.parcialmagneto.entities.Dna;
import org.demo.parcialmagneto.repositories.DnaRepository;
import org.demo.parcialmagneto.validators.DnaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DnaServiceTest {
    @InjectMocks
    private DnaService dnaService;

    @Mock
    private DnaRepository dnaRepository;

    @Mock
    private StatsService StatsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        StatsService statsService = new StatsService(dnaRepository);
        dnaService = new DnaService(dnaRepository, StatsService);
    }

    private final DnaValidator validator = new DnaValidator();
    // ====================================================================================================
    // Tests cubriendo todas las secuencias de matriz 6x6
    @Test
    public void testAnalyzeDna_AlreadyExists_ReturnsIsMutant() {
        // Preparar datos
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAGGA", "CCCCTA", "TCACTG"};
        String dnaSequence = String.join(",", dna);

        // Simular comportamiento del repositorio
        Dna existingDna = new Dna();
        existingDna.setMutant(true);
        when(dnaRepository.findByDna(dnaSequence)).thenReturn(Optional.of(existingDna));

        // Ejecutar método
        boolean result = dnaService.analyzeDna(dna);

        // Verificar resultados
        assertTrue(result);
        verify(dnaRepository, never()).save(any());
    }
    @Test
    public void testAnalyzeDna_IsMutant_SavesInRepository() {
        // Preparar datos
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAGGA", "CCCCTA", "TCACTG"};

        // Simular comportamiento del repositorio
        when(dnaRepository.findByDna(any())).thenReturn(Optional.empty());
        when(dnaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar método
        boolean result = dnaService.analyzeDna(dna);

        // Verificar resultados
        assertTrue(result);
        verify(dnaRepository).save(any());
    }
    @Test
    public void testGetStats_ReturnsCorrectStats() {
        // Simular el comportamiento del repositorio para devolver los conteos correctos
        when(dnaRepository.countByIsMutant(true)).thenReturn(5L);
        when(dnaRepository.countByIsMutant(false)).thenReturn(3L);

        // Crear el objeto StatsResponse con datos calculados
        StatsResponse expectedStats = new StatsResponse(5L, 3L, 1.6666666666666667);

        // Simular el comportamiento del servicio StatsService
        when(StatsService.getStats()).thenReturn(expectedStats);

        // Ejecutar el método
        StatsResponse actualStats = StatsService.getStats();

        // Verificar los resultados
        assertEquals(5L, actualStats.getCountMutantDna());
        assertEquals(3L, actualStats.getCountHumanDna());
        assertEquals(1.6666666666666667, actualStats.getRatio());
    }

    @Test
    public void testRows() {
        String[] dna = {
                "AAAATG",
                "TGCAGT",
                "GCTTCC",
                "CCCCTG",
                "GTAGTC",
                "AGTCAC"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testColumns() {
        String[] dna = {
                "AGAATG",
                "TGCAGT",
                "GCTTCC",
                "GTCCTC",
                "GTAGTC",
                "GGTCAC"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testMainDiagonals() {
        String[] dna = {
                "AGAATG",
                "TACAGT",
                "GCAGCC",
                "TTGATG",
                "GTAGTC",
                "AGTCAA"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testSecondaryLeftDiagonals() {
        String[] dna = {
                "ATAATG",
                "GTTAGT",
                "GGCTCG",
                "TTGATG",
                "GTAGTC",
                "AGTCAA"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testSecondaryRightDiagonals() {
        String[] dna = {
                "ATAATG",
                "GTATGA",
                "GCTTAG",
                "TTTAGG",
                "GTAGTC",
                "AGTCAA"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testTertiaryLeftDiagonals() {
        String[] dna = {
                "ATGATG",
                "GTAGTA",
                "CCTTGG",
                "TCTAGG",
                "GGCGTC",
                "AGTCAA"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testTertiaryRightDiagonals() {
        String[] dna = {
                "ATGATG",
                "GTATTA",
                "AATTGG",
                "ACTAGT",
                "GGAGTC",
                "AGGCAA"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testNonMutant() {
        String[] dna = {
                "ATGATG",
                "GTCTTA",
                "AATTGG",
                "ACTAGT",
                "GGATTC",
                "AGGCAA"
        };
        assertFalse(DnaService.isMutant(dna));
    }


    // ====================================================================================================
    // Tests brindados por el profesor
    @Test
    public void testMutant1() {
        String[] dna = {
                "AAAA",
                "CCCC",
                "TCAG",
                "GGTC"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testNonMutant1() {
        String[] dna = {
                "AAAT",
                "AACC",
                "AAAC",
                "CGGG"
        };
        assertFalse(DnaService.isMutant(dna));
    }

    @Test
    public void testMutant2() {
        String[] dna = {
                "TGAC",
                "AGCC",
                "TGAC",
                "GGTC"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testMutant3() {
        String[] dna = {
                "AAAA",
                "AAAA",
                "AAAA",
                "AAAA"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testNonMutant2() {
        String[] dna = {
                "TGAC",
                "ATCC",
                "TAAG",
                "GGTC"
        };
        assertFalse(DnaService.isMutant(dna));
    }

    @Test
    public void testMutant4() {
        String[] dna = {
                "TCGGGTGAT",
                "TGATCCTTT",
                "TACGAGTGA",
                "AAATGTACG",
                "ACGAGTGCT",
                "AGACACATG",
                "GAATTCCAA",
                "ACTACGACC",
                "TGAGTATCC"
        };
        assertTrue(DnaService.isMutant(dna));
    }

    @Test
    public void testMutant5() {
        String[] dna = {
                "TTTTTTTTT",
                "TTTTTTTTT",
                "TTTTTTTTT",
                "TTTTTTTTT",
                "CCGACCAGT",
                "GGCACTCCA",
                "AGGACACTA",
                "CAAAGGCAT",
                "GCAGTCCCC"
        };
        assertTrue(DnaService.isMutant(dna));
    }
    @Test
    public void testIsValid_NullDna() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    public void testIsValid_EmptyArray() {
        assertFalse(validator.isValid(new String[]{}, null));
    }

    @Test
    public void testIsValid_DifferentLengthSequences() {
        String[] dna = {"ATGC", "AGT", "TTATG", "AGAGGA"};
        assertFalse(validator.isValid(dna, null));
    }

    @Test
    public void testIsValid_ContainsInvalidCharacters() {
        String[] dna = {"ATGZ", "CAGT", "TTATG", "AGAGGA"};
        assertFalse(validator.isValid(dna, null));
    }

    @Test
    public void testIsValid_ValidDna() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAGGA", "CCCCTA", "TCACTG"};
        assertTrue(validator.isValid(dna, null));
    }

    @Test
    public void testIsValid_ValidDnaWithNullSequence() {
        String[] dna = {"ATGCGA", null, "TTATGT", "AGAGGA", "CCCCTA", "TCACTG"};
        assertFalse(validator.isValid(dna, null));
    }


}

