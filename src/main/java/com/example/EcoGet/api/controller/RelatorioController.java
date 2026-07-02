package com.example.EcoGet.api.controller;

import com.example.EcoGet.api.dto.RelatorioDTO;
import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Relatorio;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.service.RelatorioService;
import com.example.EcoGet.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
@CrossOrigin
public class RelatorioController {

    private final RelatorioService service;
    private final UsuarioService usuarioService;

    @GetMapping()
    public ResponseEntity get() {
        List<Relatorio> relatorios = service.getRelatorios();
        return ResponseEntity.ok(relatorios.stream().map(RelatorioDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Relatorio> relatorio = service.getRelatorioById(id);
        if (!relatorio.isPresent()) {
            return new ResponseEntity("Relatório não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(relatorio.map(RelatorioDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody RelatorioDTO dto) {
        try {
            Relatorio relatorio = converter(dto);
            relatorio = service.salvar(relatorio);
            return new ResponseEntity(relatorio, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody RelatorioDTO dto) {
        if (!service.getRelatorioById(id).isPresent()) {
            return new ResponseEntity("Relatório não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Relatorio relatorio = converter(dto);
            relatorio.setId(id);
            service.salvar(relatorio);
            return ResponseEntity.ok(relatorio);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Relatorio> relatorio = service.getRelatorioById(id);
        if (!relatorio.isPresent()) {
            return new ResponseEntity("Relatório não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(relatorio.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Relatorio converter(RelatorioDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Relatorio relatorio = modelMapper.map(dto, Relatorio.class);
        if (dto.getIdUsuario() != null) {
            Optional<Usuario> usuario = usuarioService.getUsuarioById(dto.getIdUsuario());
            relatorio.setUsuario(usuario.orElse(null));
        }
        return relatorio;
    }
}
