package com.example.EcoGet.service;

import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Relatorio;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.Model.repository.RelatorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RelatorioService {

    private RelatorioRepository repository;

    public RelatorioService(RelatorioRepository repository) {
        this.repository = repository;
    }

    public List<Relatorio> getRelatorios() {
        return repository.findAll();
    }

    public Optional<Relatorio> getRelatorioById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Relatorio salvar(Relatorio relatorio) {
        validar(relatorio);
        return repository.save(relatorio);
    }

    @Transactional
    public void excluir(Relatorio relatorio) {
        Objects.requireNonNull(relatorio.getId());
        repository.delete(relatorio);
    }

    public void validar(Relatorio relatorio) {
        if (relatorio.getTipo() == null || relatorio.getTipo().trim().equals("")) {
            throw new RegraNegocioException("Tipo inválido");
        }
        if (relatorio.getDataGeracao() == null) {
            throw new RegraNegocioException("Data de Geração inválida");
        }
        if (relatorio.getUsuario() == null || relatorio.getUsuario().getId() == null || relatorio.getUsuario().getId() == 0) {
            throw new RegraNegocioException("Usuário inválido");
        }
    }
}
