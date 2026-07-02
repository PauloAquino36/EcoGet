package com.example.EcoGet.service;

import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.ContaBancaria;
import com.example.EcoGet.Model.entity.Meta;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.Model.repository.ContaBancariaRepository;
import com.example.EcoGet.Model.repository.MetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MetaService {

    private final MetaRepository repository;
    private final ContaBancariaRepository contaBancariaRepository;

    public MetaService(MetaRepository repository, ContaBancariaRepository contaBancariaRepository) {
        this.repository = repository;
        this.contaBancariaRepository = contaBancariaRepository;
    }

    public List<Meta> getMetas() {
        return repository.findAll();
    }

    public Optional<Meta> getMetaById(Long id) {
        return repository.findById(id);
    }

    public List<Meta> getMetasByUsuario(Optional<Usuario> usuario) {
        return repository.findByUsuario(usuario);
    }

    @Transactional
    public Meta salvar(Meta meta) {
        validar(meta);
        return repository.save(meta);
    }

    @Transactional
    public void excluir(Meta meta) {
        Objects.requireNonNull(meta.getId());
        repository.delete(meta);
    }

    @Transactional
    public Meta depositar(Long metaId, Float valor) {
        if (valor == null || valor <= 0) throw new RegraNegocioException("Valor de depósito inválido");

        Meta meta = repository.findById(metaId)
                .orElseThrow(() -> new RegraNegocioException("Meta não encontrada"));

        if (meta.getContaBancaria() == null)
            throw new RegraNegocioException("Esta meta não tem conta bancária vinculada");

        ContaBancaria conta = contaBancariaRepository.findById(meta.getContaBancaria().getId())
                .orElseThrow(() -> new RegraNegocioException("Conta bancária não encontrada"));

        float saldoAtual = conta.getSaldo() != null ? conta.getSaldo() : 0f;
        if (saldoAtual < valor) throw new RegraNegocioException("Saldo insuficiente na conta bancária");

        conta.setSaldo(saldoAtual - valor);
        contaBancariaRepository.save(conta);

        float progressoAtual = meta.getProgresso() != null ? meta.getProgresso() : 0f;
        meta.setProgresso(progressoAtual + valor);
        return repository.save(meta);
    }

    @Transactional
    public Meta sacar(Long metaId, Float valor) {
        if (valor == null || valor <= 0) throw new RegraNegocioException("Valor de saque inválido");

        Meta meta = repository.findById(metaId)
                .orElseThrow(() -> new RegraNegocioException("Meta não encontrada"));

        if (meta.getContaBancaria() == null)
            throw new RegraNegocioException("Esta meta não tem conta bancária vinculada");

        float progressoAtual = meta.getProgresso() != null ? meta.getProgresso() : 0f;
        if (progressoAtual < valor) throw new RegraNegocioException("Saldo da meta insuficiente para saque");

        ContaBancaria conta = contaBancariaRepository.findById(meta.getContaBancaria().getId())
                .orElseThrow(() -> new RegraNegocioException("Conta bancária não encontrada"));

        conta.setSaldo((conta.getSaldo() != null ? conta.getSaldo() : 0f) + valor);
        contaBancariaRepository.save(conta);

        meta.setProgresso(progressoAtual - valor);
        return repository.save(meta);
    }

    public void validar(Meta meta) {
        if (meta.getNome() == null || meta.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("Nome da meta é obrigatório");
        }
        if (meta.getValor() == null || meta.getValor() <= 0) {
            throw new RegraNegocioException("Valor alvo deve ser maior que zero");
        }
        if (meta.getInicio() == null) {
            throw new RegraNegocioException("Data de início é obrigatória");
        }
        if (meta.getFim() == null) {
            throw new RegraNegocioException("Data de fim é obrigatória");
        }
        if (meta.getInicio().isAfter(meta.getFim())) {
            throw new RegraNegocioException("Data de início não pode ser posterior à data de fim");
        }
        if (meta.getUsuario() == null || meta.getUsuario().getId() == null || meta.getUsuario().getId() == 0) {
            throw new RegraNegocioException("Usuário inválido");
        }
        if (meta.getContaBancaria() == null) {
            throw new RegraNegocioException("Conta bancária é obrigatória");
        }
    }
}
