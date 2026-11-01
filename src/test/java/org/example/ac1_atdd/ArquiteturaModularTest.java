package org.example.ac1_atdd;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Desafio 1 (B1) - Evidencia do mini-lab.
 *
 * Hipotese: no Monolito Modular as dependencias entre modulos ficam explicitas
 * e controladas. Este teste le os imports do codigo-fonte e falha se alguma
 * fronteira for violada. Nao precisa de Spring nem de banco.
 *
 * Regras:
 *  1. ranking e conquistas nao dependem um do outro;
 *  2. shared nao depende de nenhum modulo de negocio;
 *  3. nenhum modulo acessa o pacote internal de outro modulo.
 */
@DisplayName("Arquitetura - fronteiras do Monolito Modular")
class ArquiteturaModularTest {

    private static final Path RAIZ = Paths.get("src/main/java/org/example/ac1_atdd");
    private static final Pattern IMPORT = Pattern.compile(
            "^import\\s+(?:static\\s+)?org\\.example\\.ac1_atdd\\.([\\w.]+)", Pattern.MULTILINE);

    /** Uma dependencia: arquivo de origem -> pacote importado (relativo a org.example.ac1_atdd). */
    private static final class Dependencia {
        final String moduloOrigem;
        final String arquivo;
        final String importado;

        Dependencia(String moduloOrigem, String arquivo, String importado) {
            this.moduloOrigem = moduloOrigem;
            this.arquivo = arquivo;
            this.importado = importado;
        }

        String moduloDestino() {
            return importado.split("\\.")[0];
        }

        @Override
        public String toString() {
            return arquivo + " -> " + importado;
        }
    }

    private static List<Dependencia> dependencias() {
        assertTrue(Files.isDirectory(RAIZ), "Codigo-fonte nao encontrado em " + RAIZ.toAbsolutePath());
        List<Dependencia> deps = new ArrayList<>();
        try (Stream<Path> arquivos = Files.walk(RAIZ)) {
            for (Path arquivo : (Iterable<Path>) arquivos.filter(p -> p.toString().endsWith(".java"))::iterator) {
                Path relativo = RAIZ.relativize(arquivo);
                String modulo = relativo.getNameCount() > 1 ? relativo.getName(0).toString() : "(raiz)";
                Matcher m = IMPORT.matcher(Files.readString(arquivo));
                while (m.find()) {
                    deps.add(new Dependencia(modulo, relativo.toString().replace('\\', '/'), m.group(1)));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return deps;
    }

    private static List<Dependencia> violacoes(String origem, String destino) {
        List<Dependencia> encontradas = new ArrayList<>();
        for (Dependencia d : dependencias()) {
            if (d.moduloOrigem.equals(origem) && d.moduloDestino().equals(destino)) encontradas.add(d);
        }
        return encontradas;
    }

    @Test
    @DisplayName("ranking nao depende de conquistas")
    void rankingNaoDependeDeConquistas() {
        List<Dependencia> v = violacoes("ranking", "conquistas");
        assertTrue(v.isEmpty(), "Violacoes: " + v);
    }

    @Test
    @DisplayName("conquistas nao depende de ranking")
    void conquistasNaoDependeDeRanking() {
        List<Dependencia> v = violacoes("conquistas", "ranking");
        assertTrue(v.isEmpty(), "Violacoes: " + v);
    }

    @Test
    @DisplayName("shared nao depende de modulos de negocio")
    void sharedNaoDependeDeModulosDeNegocio() {
        List<Dependencia> v = new ArrayList<>(violacoes("shared", "ranking"));
        v.addAll(violacoes("shared", "conquistas"));
        assertTrue(v.isEmpty(), "Violacoes: " + v);
    }

    @Test
    @DisplayName("nenhum modulo acessa o internal de outro modulo")
    void nenhumModuloAcessaInternalDeOutro() {
        List<Dependencia> v = new ArrayList<>();
        for (Dependencia d : dependencias()) {
            boolean acessaInternal = d.importado.contains(".internal.");
            if (acessaInternal && !d.moduloDestino().equals(d.moduloOrigem)) v.add(d);
        }
        assertTrue(v.isEmpty(), "Violacoes: " + v);
    }

    @Test
    @DisplayName("imprime a matriz de dependencias entre modulos (evidencia)")
    void imprimeMatrizDeDependencias() {
        Map<String, TreeSet<String>> matriz = new TreeMap<>();
        for (Dependencia d : dependencias()) {
            if (!d.moduloDestino().equals(d.moduloOrigem)) {
                matriz.computeIfAbsent(d.moduloOrigem, k -> new TreeSet<>()).add(d.moduloDestino());
            }
        }
        System.out.println("=== Dependencias entre modulos ===");
        matriz.forEach((origem, destinos) -> System.out.println(origem + " -> " + destinos));
        assertTrue(matriz.getOrDefault("shared", new TreeSet<>()).isEmpty(), "shared deveria ser folha");
    }
}
