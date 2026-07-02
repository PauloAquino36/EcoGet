package com.example.EcoGet.api.controller;

import com.example.EcoGet.api.dto.ContaBancariaDTO;
import com.example.EcoGet.api.dto.TransacaoDTO;
import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Banco;
import com.example.EcoGet.Model.entity.ContaBancaria;
import com.example.EcoGet.Model.entity.Transacao;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.service.BancoService;
import com.example.EcoGet.service.ContaBancariaService;
import com.example.EcoGet.service.TransacaoService;
import com.example.EcoGet.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contasbancarias")
@RequiredArgsConstructor
@CrossOrigin
public class ContaBancariaController {

    private final ContaBancariaService service;
    private final UsuarioService usuarioService;
    private final BancoService bancoService;
    private final TransacaoService transacaoService;

    @GetMapping()
    public ResponseEntity get() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuario = usuarioService.getUsuarioByEmail(email);
        List<ContaBancaria> contas = service.getContasBancariasByUsuario(usuario);
        return ResponseEntity.ok(contas.stream().map(ContaBancariaDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<ContaBancaria> conta = service.getContaBancariaById(id);
        if (!conta.isPresent()) {
            return new ResponseEntity("Conta Bancária não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(conta.map(ContaBancariaDTO::create));
    }

    @GetMapping("{id}/transacoes")
    public ResponseEntity getTransacoes(@PathVariable("id") Long id) {
        Optional<ContaBancaria> conta = service.getContaBancariaById(id);
        if (!conta.isPresent()) {
            return new ResponseEntity("Conta Bancária não encontrada", HttpStatus.NOT_FOUND);
        }
        Optional<Usuario> usuario = usuarioService.getUsuarioById(conta.get().getUsuario().getId());
        List<Transacao> transacoes = transacaoService.getTransacoesByUsuario(usuario);
        return ResponseEntity.ok(transacoes.stream().map(TransacaoDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody ContaBancariaDTO dto) {
        try {
            ContaBancaria conta = converter(dto);
            conta = service.salvar(conta);
            return new ResponseEntity(conta, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody ContaBancariaDTO dto) {
        if (!service.getContaBancariaById(id).isPresent()) {
            return new ResponseEntity("Conta Bancária não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            ContaBancaria conta = converter(dto);
            conta.setId(id);
            service.salvar(conta);
            return ResponseEntity.ok(conta);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<ContaBancaria> conta = service.getContaBancariaById(id);
        if (!conta.isPresent()) {
            return new ResponseEntity("Conta Bancária não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(conta.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ContaBancaria converter(ContaBancariaDTO dto) {
        ContaBancaria conta = new ContaBancaria();
        conta.setAgencia(dto.getAgencia());
        conta.setConta(dto.getConta());
        conta.setSaldo(dto.getSaldo());
        if (dto.getIdBanco() != null) {
            Optional<Banco> banco = bancoService.getBancoById(dto.getIdBanco());
            conta.setBanco(banco.orElse(null));
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        usuarioService.getUsuarioByEmail(email).ifPresent(conta::setUsuario);
        return conta;
    }
}
