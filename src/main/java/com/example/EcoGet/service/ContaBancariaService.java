package com.example.EcoGet.service;

import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.ContaBancaria;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.Model.repository.ContaBancariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ContaBancariaService {

    private ContaBancariaRepository repository;

    public ContaBancariaService(ContaBancariaRepository repository) {
        this.repository = repository;
    }

    public List<ContaBancaria> getContasBancarias() {
        return repository.findAll();
    }

    public Optional<ContaBancaria> getContaBancariaById(Long id) {
        return repository.findById(id);
    }

    public List<ContaBancaria> getContasBancariasByUsuario(Optional<Usuario> usuario) {
        return repository.findByUsuario(usuario);
    }

    @Transactional
    public ContaBancaria salvar(ContaBancaria contaBancaria) {
        validar(contaBancaria);
        return repository.save(contaBancaria);
    }

    @Transactional
    public void excluir(ContaBancaria contaBancaria) {
        Objects.requireNonNull(contaBancaria.getId());
        repository.delete(contaBancaria);
    }

    public void validar(ContaBancaria contaBancaria) {
        if (contaBancaria.getAgencia() == null || contaBancaria.getAgencia().trim().isEmpty()) {
            throw new RegraNegocioException("Agência é obrigatória");
        }
        if (!contaBancaria.getAgencia().trim().matches("\\d{4,6}")) {
            throw new RegraNegocioException("Agência inválida: deve conter entre 4 e 6 dígitos numéricos");
        }
        if (contaBancaria.getConta() == null || contaBancaria.getConta().trim().isEmpty()) {
            throw new RegraNegocioException("Número de conta é obrigatório");
        }
        if (!contaBancaria.getConta().trim().matches("[\\d\\-]{4,20}")) {
            throw new RegraNegocioException("Número de conta inválido: use apenas dígitos e hífens (4 a 20 caracteres)");
        }
        if (contaBancaria.getSaldo() != null && contaBancaria.getSaldo() < 0) {
            throw new RegraNegocioException("Saldo inicial não pode ser negativo");
        }
        if (contaBancaria.getBanco() == null || contaBancaria.getBanco().getId() == null || contaBancaria.getBanco().getId() == 0) {
            throw new RegraNegocioException("Banco é obrigatório");
        }
        if (contaBancaria.getUsuario() == null || contaBancaria.getUsuario().getId() == null || contaBancaria.getUsuario().getId() == 0) {
            throw new RegraNegocioException("Usuário inválido");
        }
    }
}
