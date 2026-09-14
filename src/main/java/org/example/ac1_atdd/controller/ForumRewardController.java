package org.example.ac1_atdd.controller;

import org.example.ac1_atdd.domain.*;
import org.example.ac1_atdd.service.ForumRewardService;
import org.example.ac1_atdd.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/forum-rewards")
public class ForumRewardController {

    @Autowired
    private ForumRewardService forumRewardService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ResultadoRepository resultadoRepository;

    @PostMapping("/ranking/process")
    public ResponseEntity<Resultado> processarRankingMensal(
            @RequestParam(name = "cursoId") Long cursoId) {

        List<Aluno> alunos = alunoRepository.findAll();
        Curso curso = cursoRepository.findById(cursoId).orElseThrow();

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, curso);
        Resultado saved = resultadoRepository.save(resultado);

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/ranking/process-custom")
    public ResponseEntity<Resultado> processarRankingCustom(
            @RequestParam(name = "cursoId") Long cursoId,
            @RequestParam(name = "minimo", defaultValue = "3") Long minimo) {

        List<Aluno> alunos = alunoRepository.findAll();
        Curso curso = cursoRepository.findById(cursoId).orElseThrow();

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, curso, minimo);
        Resultado saved = resultadoRepository.save(resultado);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/ranking/{id}")
    public ResponseEntity<Resultado> obterRanking(@PathVariable Long id) {
        return resultadoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/alunos")
    public ResponseEntity<List<Aluno>> listarAlunos() {
        return ResponseEntity.ok(alunoRepository.findAll());
    }

    @PostMapping("/alunos")
    public ResponseEntity<Aluno> criarAluno(@RequestBody Aluno aluno) {
        Aluno saved = alunoRepository.save(aluno);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/cursos")
    public ResponseEntity<List<Curso>> listarCursos() {
        return ResponseEntity.ok(cursoRepository.findAll());
    }

    @PostMapping("/cursos")
    public ResponseEntity<Curso> criarCurso(@RequestBody Curso curso) {
        Curso saved = cursoRepository.save(curso);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/alunos/{alunoId}/comentarios")
    public ResponseEntity<Aluno> adicionarComentario(
            @PathVariable Long alunoId,
            @RequestParam(name = "util", defaultValue = "true") boolean util,
            @RequestParam(name = "texto") String texto) {

        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow();
        Comentario comentario = new Comentario(texto, util);

        if (util) {
            aluno.adicionarComentarioUtil(comentario);
        } else {
            aluno.adicionarComentarioNaoUtil(comentario);
        }

        Aluno updated = alunoRepository.save(aluno);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/alunos/{alunoId}/score")
    public ResponseEntity<Long> obterPontuacaoAluno(@PathVariable Long alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow();
        long pontuacao = aluno.getComentariosUteis() - aluno.getComentariosNaoUteis();
        return ResponseEntity.ok(pontuacao);
    }
}