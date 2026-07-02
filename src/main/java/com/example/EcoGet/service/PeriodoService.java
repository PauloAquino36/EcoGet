package com.example.EcoGet.service;

import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Periodo;
import com.example.EcoGet.Model.repository.PeriodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PeriodoService {

    private PeriodoRepository repository;

    public PeriodoService(PeriodoRepository repository) {
        this.repository = repository;
    }

    public List<Periodo> getPeriodos() {
        return repository.findAll();
    }

    public Optional<Periodo> getPeriodoById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Periodo salvar(Periodo periodo) {
        validar(periodo);
        return repository.save(periodo);
    }

    @Transactional
    public void excluir(Periodo periodo) {
        Objects.requireNonNull(periodo.getId());
        repository.delete(periodo);
    }

    public void validar(Periodo periodo) {
        if (periodo.getInicial() == null) {
            throw new RegraNegocioException("Data inicial inválida");
        }
        if (periodo.getFim() == null) {
            throw new RegraNegocioException("Data fim inválida");
        }
        if (periodo.getInicial().isAfter(periodo.getFim())) {
            throw new RegraNegocioException("Data inicial não pode ser posterior à data fim");
        }
        if (periodo.getPeriodoOrcamental() == null || periodo.getPeriodoOrcamental().getId() == null) {
            throw new RegraNegocioException("Período Orçamental inválido");
        }
    }
}
