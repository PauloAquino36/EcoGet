package com.example.EcoGet.api.controller;

import com.example.EcoGet.api.dto.OrcamentoDTO;
import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Categoria;
import com.example.EcoGet.Model.entity.Orcamento;
import com.example.EcoGet.Model.entity.PeriodoOrcamental;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.service.CategoriaService;
import com.example.EcoGet.service.OrcamentoService;
import com.example.EcoGet.service.PeriodoOrcamentalService;
import com.example.EcoGet.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orcamentos")
@RequiredArgsConstructor
@CrossOrigin
public class OrcamentoController {

    private final OrcamentoService service;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final PeriodoOrcamentalService periodoOrcamentalService;

    @GetMapping()
    public ResponseEntity get() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuario = usuarioService.getUsuarioByEmail(email);
        List<Orcamento> orcamentos = service.getOrcamentosByUsuario(usuario);
        return ResponseEntity.ok(orcamentos.stream().map(OrcamentoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Orcamento> orcamento = service.getOrcamentoById(id);
        if (!orcamento.isPresent()) {
            return new ResponseEntity("Orçamento não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(orcamento.map(OrcamentoDTO::create));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity getByUsuario(@PathVariable("idUsuario") Long idUsuario) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(idUsuario);
        if (!usuario.isPresent()) {
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        List<Orcamento> orcamentos = service.getOrcamentosByUsuario(usuario);
        return ResponseEntity.ok(orcamentos.stream().map(OrcamentoDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody OrcamentoDTO dto) {
        try {
            Orcamento orcamento = converter(dto);
            orcamento = service.salvar(orcamento);
            return new ResponseEntity(orcamento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody OrcamentoDTO dto) {
        if (!service.getOrcamentoById(id).isPresent()) {
            return new ResponseEntity("Orçamento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Orcamento orcamento = converter(dto);
            orcamento.setId(id);
            service.salvar(orcamento);
            return ResponseEntity.ok(orcamento);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Orcamento> orcamento = service.getOrcamentoById(id);
        if (!orcamento.isPresent()) {
            return new ResponseEntity("Orçamento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(orcamento.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Orcamento converter(OrcamentoDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Orcamento orcamento = modelMapper.map(dto, Orcamento.class);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        usuarioService.getUsuarioByEmail(email).ifPresent(orcamento::setUsuario);
        if (dto.getIdCategoria() != null) {
            Optional<Categoria> categoria = categoriaService.getCategoriaById(dto.getIdCategoria());
            orcamento.setCategoria(categoria.orElse(null));
        }
        if (dto.getIdPeriodoOrcamental() != null) {
            Optional<PeriodoOrcamental> po = periodoOrcamentalService.getPeriodoOrcamentalById(dto.getIdPeriodoOrcamental());
            orcamento.setPeriodoOrcamental(po.orElse(null));
        }
        return orcamento;
    }
}
