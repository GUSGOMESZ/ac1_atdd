package org.example.ac1_atdd.conquistas.internal.service;

import org.example.ac1_atdd.conquistas.internal.domain.Conquista;
import org.example.ac1_atdd.conquistas.internal.repository.ConquistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConquistasService {

    @Autowired
    private ConquistaRepository conquistaRepository;

    public List<Conquista> listarConquistas() {
        return conquistaRepository.findAll();
    }

    public Conquista criarConquista(Conquista conquista) {
        return conquistaRepository.save(conquista);
    }

    public Conquista buscarPorId(Long id) {
        return conquistaRepository.findById(id).orElseThrow();
    }
}
