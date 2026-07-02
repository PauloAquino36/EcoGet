package com.example.EcoGet.api.controller;

import com.example.EcoGet.api.dto.TipoTransacaoDTO;
import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.TipoTransacao;
import com.example.EcoGet.service.TipoTransacaoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tipostransacao")
@RequiredArgsConstructor
@CrossOrigin
public class TipoTransacaoController {

    private final TipoTransacaoService service;

    @GetMapping()
    public ResponseEntity get() {
        List<TipoTransacao> tipos = service.getTiposTransacao();
        return ResponseEntity.ok(tipos.stream().map(TipoTransacaoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<TipoTransacao> tipo = service.getTipoTransacaoById(id);
        if (!tipo.isPresent()) {
            return new ResponseEntity("Tipo de Transação não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(tipo.map(TipoTransacaoDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody TipoTransacaoDTO dto) {
        try {
            TipoTransacao tipo = converter(dto);
            tipo = service.salvar(tipo);
            return new ResponseEntity(tipo, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody TipoTransacaoDTO dto) {
        if (!service.getTipoTransacaoById(id).isPresent()) {
            return new ResponseEntity("Tipo de Transação não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            TipoTransacao tipo = converter(dto);
            tipo.setId(id);
            service.salvar(tipo);
            return ResponseEntity.ok(tipo);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<TipoTransacao> tipo = service.getTipoTransacaoById(id);
        if (!tipo.isPresent()) {
            return new ResponseEntity("Tipo de Transação não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(tipo.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public TipoTransacao converter(TipoTransacaoDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, TipoTransacao.class);
    }
}
