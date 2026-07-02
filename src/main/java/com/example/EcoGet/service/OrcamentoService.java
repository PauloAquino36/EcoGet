package com.example.EcoGet.service;

import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Orcamento;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.Model.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrcamentoService {

    private OrcamentoRepository repository;

    public OrcamentoService(OrcamentoRepository repository) {
        this.repository = repository;
    }

    public List<Orcamento> getOrcamentos() {
        return repository.findAll();
    }

    public Optional<Orcamento> getOrcamentoById(Long id) {
        return repository.findById(id);
    }

    public List<Orcamento> getOrcamentosByUsuario(Optional<Usuario> usuario) {
        return repository.findByUsuario(usuario);
    }

    @Transactional
    public Orcamento salvar(Orcamento orcamento) {
        validar(orcamento);
        return repository.save(orcamento);
    }

    @Transactional
    public void excluir(Orcamento orcamento) {
        Objects.requireNonNull(orcamento.getId());
        repository.delete(orcamento);
    }

    public void validar(Orcamento orcamento) {
        if (orcamento.getNome() == null || orcamento.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("Nome do orçamento é obrigatório");
        }
        if (orcamento.getValorEstimado() == null || orcamento.getValorEstimado() <= 0) {
            throw new RegraNegocioException("Valor estimado deve ser maior que zero");
        }
        if (orcamento.getUsuario() == null || orcamento.getUsuario().getId() == null || orcamento.getUsuario().getId() == 0) {
            throw new RegraNegocioException("Usuário inválido");
        }
        if (orcamento.getCategoria() == null || orcamento.getCategoria().getId() == null || orcamento.getCategoria().getId() == 0) {
            throw new RegraNegocioException("Categoria é obrigatória");
        }
        List<Orcamento> existentes = repository.findByUsuario_IdAndCategoria_Id(
                orcamento.getUsuario().getId(), orcamento.getCategoria().getId());
        boolean conflito = existentes.stream()
                .anyMatch(o -> !o.getId().equals(orcamento.getId()));
        if (conflito) {
            throw new RegraNegocioException("Já existe um orçamento com esta categoria");
        }
    }
}
