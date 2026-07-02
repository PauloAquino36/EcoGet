package com.example.EcoGet.service;

import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.PeriodoOrcamental;
import com.example.EcoGet.Model.repository.PeriodoOrcamentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PeriodoOrcamentalService {

    private PeriodoOrcamentalRepository repository;

    public PeriodoOrcamentalService(PeriodoOrcamentalRepository repository) {
        this.repository = repository;
    }

    public List<PeriodoOrcamental> getPeriodosOrcamentais() {
        return repository.findAll();
    }

    public Optional<PeriodoOrcamental> getPeriodoOrcamentalById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public PeriodoOrcamental salvar(PeriodoOrcamental periodoOrcamental) {
        validar(periodoOrcamental);
        return repository.save(periodoOrcamental);
    }

    @Transactional
    public void excluir(PeriodoOrcamental periodoOrcamental) {
        Objects.requireNonNull(periodoOrcamental.getId());
        repository.delete(periodoOrcamental);
    }

    public void validar(PeriodoOrcamental periodoOrcamental) {
        if (periodoOrcamental.getTipo() == null || periodoOrcamental.getTipo().trim().equals("")) {
            throw new RegraNegocioException("Tipo inválido");
        }
    }
}
