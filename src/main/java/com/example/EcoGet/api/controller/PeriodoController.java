package com.example.EcoGet.api.controller;

import com.example.EcoGet.api.dto.PeriodoDTO;
import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Periodo;
import com.example.EcoGet.Model.entity.PeriodoOrcamental;
import com.example.EcoGet.service.PeriodoOrcamentalService;
import com.example.EcoGet.service.PeriodoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/periodos")
@RequiredArgsConstructor
@CrossOrigin
public class PeriodoController {

    private final PeriodoService service;
    private final PeriodoOrcamentalService periodoOrcamentalService;

    @GetMapping()
    public ResponseEntity get() {
        List<Periodo> periodos = service.getPeriodos();
        return ResponseEntity.ok(periodos.stream().map(PeriodoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Periodo> periodo = service.getPeriodoById(id);
        if (!periodo.isPresent()) {
            return new ResponseEntity("Período não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(periodo.map(PeriodoDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody PeriodoDTO dto) {
        try {
            Periodo periodo = converter(dto);
            periodo = service.salvar(periodo);
            return new ResponseEntity(periodo, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody PeriodoDTO dto) {
        if (!service.getPeriodoById(id).isPresent()) {
            return new ResponseEntity("Período não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Periodo periodo = converter(dto);
            periodo.setId(id);
            service.salvar(periodo);
            return ResponseEntity.ok(periodo);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Periodo> periodo = service.getPeriodoById(id);
        if (!periodo.isPresent()) {
            return new ResponseEntity("Período não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(periodo.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Periodo converter(PeriodoDTO dto) {
        Periodo periodo = new Periodo();
        periodo.setId(dto.getId());

        if (dto.getIdPeriodoOrcamental() == null) {
            throw new RegraNegocioException("Período Orçamental inválido");
        }

        Optional<PeriodoOrcamental> periodoOrcamental = periodoOrcamentalService.getPeriodoOrcamentalById(dto.getIdPeriodoOrcamental());
        if (!periodoOrcamental.isPresent()) {
            throw new RegraNegocioException("Período Orçamental não encontrado");
        }

        PeriodoOrcamental po = periodoOrcamental.get();
        periodo.setPeriodoOrcamental(po);

        LocalDate inicio = (dto.getInicial() != null) ? dto.getInicial() : LocalDate.now();
        periodo.setInicial(inicio);
        periodo.setFim(calcularFim(inicio, po.getTipo()));

        return periodo;
    }

    private LocalDate calcularFim(LocalDate inicio, String tipo) {
        if (tipo == null) return inicio.plusMonths(1);
        return switch (tipo.toLowerCase().trim()) {
            case "diario", "diário"         -> inicio.plusDays(1);
            case "semanal"                  -> inicio.plusWeeks(1);
            case "quinzenal"                -> inicio.plusDays(15);
            case "mensal"                   -> inicio.plusMonths(1);
            case "bimestral"                -> inicio.plusMonths(2);
            case "trimestral"               -> inicio.plusMonths(3);
            case "semestral"                -> inicio.plusMonths(6);
            case "anual"                    -> inicio.plusYears(1);
            default                         -> inicio.plusMonths(1);
        };
    }
}
