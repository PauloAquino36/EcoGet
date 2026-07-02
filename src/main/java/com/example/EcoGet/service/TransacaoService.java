package com.example.EcoGet.service;

import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.ContaBancaria;
import com.example.EcoGet.Model.entity.Orcamento;
import com.example.EcoGet.Model.entity.TipoTransacao;
import com.example.EcoGet.Model.entity.Transacao;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.Model.repository.ContaBancariaRepository;
import com.example.EcoGet.Model.repository.OrcamentoRepository;
import com.example.EcoGet.Model.repository.TipoTransacaoRepository;
import com.example.EcoGet.Model.repository.TransacaoRepository;
import com.example.EcoGet.Model.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransacaoService {

    private final TransacaoRepository repository;
    private final ContaBancariaRepository contaBancariaRepository;
    private final TipoTransacaoRepository tipoTransacaoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoService(TransacaoRepository repository,
                            ContaBancariaRepository contaBancariaRepository,
                            TipoTransacaoRepository tipoTransacaoRepository,
                            OrcamentoRepository orcamentoRepository,
                            UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.contaBancariaRepository = contaBancariaRepository;
        this.tipoTransacaoRepository = tipoTransacaoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Transacao> getTransacoes() {
        return repository.findAll();
    }

    public Optional<Transacao> getTransacaoById(Long id) {
        return repository.findById(id);
    }

    public List<Transacao> getTransacoesByUsuario(Optional<Usuario> usuario) {
        return repository.findByUsuario(usuario);
    }

    @Transactional
    public Transacao salvar(Transacao transacao) {
        validar(transacao);

        // Atualização: reverter impacto da transação anterior antes de salvar a nova
        if (transacao.getId() != null) {
            repository.findById(transacao.getId()).ifPresent(this::reverterImpactoSaldo);
        }

        Transacao saved = repository.save(transacao);
        aplicarImpactoSaldo(saved);
        return saved;
    }

    @Transactional
    public void excluir(Transacao transacao) {
        Objects.requireNonNull(transacao.getId());
        reverterImpactoSaldo(transacao);
        repository.delete(transacao);
    }

    // Aplica o valor da transação no saldo da conta (crédito soma, débito subtrai)
    private void aplicarImpactoSaldo(Transacao transacao) {
        if (transacao.getContaBancaria() == null || transacao.getTipoTransacao() == null) return;

        // Reler o TipoTransacao do DB para garantir o valor de 'credito'
        TipoTransacao tipo = tipoTransacaoRepository
                .findById(transacao.getTipoTransacao().getId())
                .orElse(null);
        if (tipo == null || tipo.getCredito() == null) return;

        contaBancariaRepository.findById(transacao.getContaBancaria().getId()).ifPresent(conta -> {
            float saldo = conta.getSaldo() != null ? conta.getSaldo() : 0f;
            float valor = transacao.getValor() != null ? transacao.getValor() : 0f;
            conta.setSaldo(tipo.getCredito() ? saldo + valor : saldo - valor);
            contaBancariaRepository.save(conta);
            if (conta.getUsuario() != null) atualizarSaldoUsuario(conta.getUsuario().getId());
        });

        // Atualizar orçamentos da categoria (débito aumenta valorAtual, crédito diminui)
        atualizarOrcamentos(transacao, tipo, false);
    }

    // Reverte o impacto anterior (oposto de aplicar)
    private void reverterImpactoSaldo(Transacao transacao) {
        if (transacao.getContaBancaria() == null || transacao.getTipoTransacao() == null) return;

        TipoTransacao tipo = tipoTransacaoRepository
                .findById(transacao.getTipoTransacao().getId())
                .orElse(null);
        if (tipo == null || tipo.getCredito() == null) return;

        contaBancariaRepository.findById(transacao.getContaBancaria().getId()).ifPresent(conta -> {
            float saldo = conta.getSaldo() != null ? conta.getSaldo() : 0f;
            float valor = transacao.getValor() != null ? transacao.getValor() : 0f;
            conta.setSaldo(tipo.getCredito() ? saldo - valor : saldo + valor);
            contaBancariaRepository.save(conta);
            if (conta.getUsuario() != null) atualizarSaldoUsuario(conta.getUsuario().getId());
        });

        atualizarOrcamentos(transacao, tipo, true);
    }

    // Recalcula saldoFinal do usuário como soma de todos os saldos de suas contas
    private void atualizarSaldoUsuario(Long usuarioId) {
        List<ContaBancaria> contas = contaBancariaRepository.findByUsuario_Id(usuarioId);
        float total = 0f;
        for (ContaBancaria c : contas) {
            if (c.getSaldo() != null) total += c.getSaldo();
        }
        final float saldoTotal = total;
        usuarioRepository.findById(usuarioId).ifPresent(u -> {
            u.setSaldoFinal(saldoTotal);
            usuarioRepository.save(u);
        });
    }

    // Débito aumenta o valorAtual do orçamento; crédito diminui (ex: estorno)
    private void atualizarOrcamentos(Transacao transacao, TipoTransacao tipo, boolean reverter) {
        if (transacao.getCategoria() == null || transacao.getCategoria().getId() == null) return;
        List<Orcamento> orcamentos = orcamentoRepository.findByCategoria_Id(transacao.getCategoria().getId());
        float valor = transacao.getValor() != null ? transacao.getValor() : 0f;
        for (Orcamento o : orcamentos) {
            float atual = o.getValorAtual() != null ? o.getValorAtual() : 0f;
            // Débito (credito=false) aumenta gasto; crédito diminui
            float delta = tipo.getCredito() ? -valor : valor;
            if (reverter) delta = -delta;
            o.setValorAtual(atual + delta);
            orcamentoRepository.save(o);
        }
    }

    public void validar(Transacao transacao) {
        if (transacao.getValor() == null || transacao.getValor() <= 0) {
            throw new RegraNegocioException("Valor inválido: deve ser maior que zero");
        }
        if (transacao.getData() == null) {
            throw new RegraNegocioException("Data inválida");
        }
        if (transacao.getDescricao() == null || transacao.getDescricao().trim().equals("")) {
            throw new RegraNegocioException("Descrição inválida");
        }
        if (transacao.getTipoTransacao() == null || transacao.getTipoTransacao().getId() == null || transacao.getTipoTransacao().getId() == 0) {
            throw new RegraNegocioException("Tipo de Transação inválido");
        }
        TipoTransacao tipo = tipoTransacaoRepository.findById(transacao.getTipoTransacao().getId()).orElse(null);
        if (tipo == null || tipo.getCredito() == null) {
            throw new RegraNegocioException("O Tipo de Transação selecionado não tem crédito/débito definido. Atualize-o antes de criar a transação.");
        }
        if (transacao.getContaBancaria() == null || transacao.getContaBancaria().getId() == null) {
            throw new RegraNegocioException("Conta Bancária é obrigatória");
        }
        if (transacao.getUsuario() == null || transacao.getUsuario().getId() == null || transacao.getUsuario().getId() == 0) {
            throw new RegraNegocioException("Usuário inválido");
        }
        // Validar saldo suficiente para débito
        if (!tipo.getCredito()) {
            ContaBancaria conta = contaBancariaRepository.findById(transacao.getContaBancaria().getId())
                    .orElseThrow(() -> new RegraNegocioException("Conta Bancária não encontrada"));
            float saldoDisponivel = conta.getSaldo() != null ? conta.getSaldo() : 0f;
            // Em atualização, projetar o saldo após reverter o impacto da transação anterior
            if (transacao.getId() != null) {
                Transacao antiga = repository.findById(transacao.getId()).orElse(null);
                if (antiga != null && antiga.getTipoTransacao() != null && antiga.getValor() != null) {
                    TipoTransacao tipoAntigo = tipoTransacaoRepository
                            .findById(antiga.getTipoTransacao().getId()).orElse(null);
                    if (tipoAntigo != null && tipoAntigo.getCredito() != null) {
                        saldoDisponivel += tipoAntigo.getCredito() ? -antiga.getValor() : antiga.getValor();
                    }
                }
            }
            if (transacao.getValor() > saldoDisponivel) {
                throw new RegraNegocioException("Saldo insuficiente: disponível R$ "
                        + String.format("%.2f", saldoDisponivel));
            }
        }
    }
}
