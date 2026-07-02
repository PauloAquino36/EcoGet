package com.example.EcoGet.api.controller;

import com.example.EcoGet.api.dto.PeriodoOrcamentalDTO;
import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.PeriodoOrcamental;
import com.example.EcoGet.service.PeriodoOrcamentalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/periodosorcamentais")
@RequiredArgsConstructor
@CrossOrigin
public class PeriodoOrcamentalController {

    private final PeriodoOrcamentalService service;

    @GetMapping()
    public ResponseEntity get() {
        List<PeriodoOrcamental> periodos = service.getPeriodosOrcamentais();
        return ResponseEntity.ok(periodos.stream().map(PeriodoOrcamentalDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<PeriodoOrcamental> periodo = service.getPeriodoOrcamentalById(id);
        if (!periodo.isPresent()) {
            return new ResponseEntity("Período Orçamental não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(periodo.map(PeriodoOrcamentalDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody PeriodoOrcamentalDTO dto) {
        try {
            PeriodoOrcamental periodo = converter(dto);
            periodo = service.salvar(periodo);
            return new ResponseEntity(periodo, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody PeriodoOrcamentalDTO dto) {
        if (!service.getPeriodoOrcamentalById(id).isPresent()) {
            return new ResponseEntity("Período Orçamental não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            PeriodoOrcamental periodo = converter(dto);
            periodo.setId(id);
            service.salvar(periodo);
            return ResponseEntity.ok(periodo);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<PeriodoOrcamental> periodo = service.getPeriodoOrcamentalById(id);
        if (!periodo.isPresent()) {
            return new ResponseEntity("Período Orçamental não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(periodo.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public PeriodoOrcamental converter(PeriodoOrcamentalDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, PeriodoOrcamental.class);
    }
}
