package com.example.EcoGet.service;

import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.TipoTransacao;
import com.example.EcoGet.Model.repository.TipoTransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TipoTransacaoService {

    private TipoTransacaoRepository repository;

    public TipoTransacaoService(TipoTransacaoRepository repository) {
        this.repository = repository;
    }

    public List<TipoTransacao> getTiposTransacao() {
        return repository.findAll();
    }

    public Optional<TipoTransacao> getTipoTransacaoById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public TipoTransacao salvar(TipoTransacao tipoTransacao) {
        validar(tipoTransacao);
        return repository.save(tipoTransacao);
    }

    @Transactional
    public void excluir(TipoTransacao tipoTransacao) {
        Objects.requireNonNull(tipoTransacao.getId());
        repository.delete(tipoTransacao);
    }

    public void validar(TipoTransacao tipoTransacao) {
        if (tipoTransacao.getNome() == null || tipoTransacao.getNome().trim().equals("")) {
            throw new RegraNegocioException("Nome inválido");
        }
        if (tipoTransacao.getCredito() == null) {
            throw new RegraNegocioException("Informe se o tipo de transação é crédito ou débito");
        }
    }
}
