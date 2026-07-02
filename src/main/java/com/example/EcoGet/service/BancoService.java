package com.example.EcoGet.service;

import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Banco;
import com.example.EcoGet.Model.repository.BancoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BancoService {

    private BancoRepository repository;

    public BancoService(BancoRepository repository) {
        this.repository = repository;
    }

    public List<Banco> getBancos() {
        return repository.findAll();
    }

    public Optional<Banco> getBancoById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Banco salvar(Banco banco) {
        validar(banco);
        return repository.save(banco);
    }

    @Transactional
    public void excluir(Banco banco) {
        Objects.requireNonNull(banco.getId());
        repository.delete(banco);
    }

    public void validar(Banco banco) {
        if (banco.getNome() == null || banco.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("Nome do banco é obrigatório");
        }
        if (banco.getCodigo() == null || banco.getCodigo().trim().isEmpty()) {
            throw new RegraNegocioException("Código do banco é obrigatório");
        }
        if (!banco.getCodigo().trim().matches("\\d{3}")) {
            throw new RegraNegocioException("Código do banco deve ter exatamente 3 dígitos numéricos");
        }
    }
}
