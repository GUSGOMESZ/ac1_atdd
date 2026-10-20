package org.example.ac1_atdd.conquistas.api;

import org.example.ac1_atdd.conquistas.internal.domain.Conquista;
import org.example.ac1_atdd.conquistas.internal.service.ConquistasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/conquistas")
public class ConquistasController {

    @Autowired
    private ConquistasService conquistasService;

    @GetMapping
    public ResponseEntity<List<Conquista>> listarConquistas() {
        return ResponseEntity.ok(conquistasService.listarConquistas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conquista> buscarConquista(@PathVariable Long id) {
        return ResponseEntity.ok(conquistasService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Conquista> criarConquista(@RequestBody Conquista conquista) {
        return ResponseEntity.ok(conquistasService.criarConquista(conquista));
    }
}
