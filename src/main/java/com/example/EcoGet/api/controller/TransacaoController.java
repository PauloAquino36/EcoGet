package com.example.EcoGet.api.controller;

import com.example.EcoGet.api.dto.TransacaoDTO;
import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Categoria;
import com.example.EcoGet.Model.entity.ContaBancaria;
import com.example.EcoGet.Model.entity.TipoTransacao;
import com.example.EcoGet.Model.entity.Transacao;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.service.CategoriaService;
import com.example.EcoGet.service.ContaBancariaService;
import com.example.EcoGet.service.TipoTransacaoService;
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
@RequestMapping("/api/v1/transacoes")
@RequiredArgsConstructor
@CrossOrigin
public class TransacaoController {

    private final TransacaoService service;
    private final UsuarioService usuarioService;
    private final TipoTransacaoService tipoTransacaoService;
    private final CategoriaService categoriaService;
    private final ContaBancariaService contaBancariaService;

    @GetMapping()
    public ResponseEntity get() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuario = usuarioService.getUsuarioByEmail(email);
        List<Transacao> transacoes = service.getTransacoesByUsuario(usuario);
        return ResponseEntity.ok(transacoes.stream().map(TransacaoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Transacao> transacao = service.getTransacaoById(id);
        if (!transacao.isPresent()) {
            return new ResponseEntity("Transação não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(transacao.map(TransacaoDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody TransacaoDTO dto) {
        try {
            Transacao transacao = converter(dto);
            transacao = service.salvar(transacao);
            return new ResponseEntity(transacao, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody TransacaoDTO dto) {
        if (!service.getTransacaoById(id).isPresent()) {
            return new ResponseEntity("Transação não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            Transacao transacao = converter(dto);
            transacao.setId(id);
            service.salvar(transacao);
            return ResponseEntity.ok(transacao);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Transacao> transacao = service.getTransacaoById(id);
        if (!transacao.isPresent()) {
            return new ResponseEntity("Transação não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(transacao.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Transacao converter(TransacaoDTO dto) {
        Transacao transacao = new Transacao();
        transacao.setValor(dto.getValor());
        transacao.setData(dto.getData());
        transacao.setDescricao(dto.getDescricao());
        transacao.setRecorrente(dto.isRecorrente());
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        usuarioService.getUsuarioByEmail(email).ifPresent(transacao::setUsuario);
        if (dto.getIdTipoTransacao() != null) {
            Optional<TipoTransacao> tipoTransacao = tipoTransacaoService.getTipoTransacaoById(dto.getIdTipoTransacao());
            transacao.setTipoTransacao(tipoTransacao.orElse(null));
        }
        if (dto.getIdCategoria() != null) {
            Optional<Categoria> categoria = categoriaService.getCategoriaById(dto.getIdCategoria());
            transacao.setCategoria(categoria.orElse(null));
        }
        if (dto.getIdContaBancaria() != null) {
            Optional<ContaBancaria> contaBancaria = contaBancariaService.getContaBancariaById(dto.getIdContaBancaria());
            transacao.setContaBancaria(contaBancaria.orElse(null));
        }
        return transacao;
    }
}
