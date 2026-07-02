package com.example.EcoGet.api.controller;

import com.example.EcoGet.api.dto.BancoDTO;
import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.Banco;
import com.example.EcoGet.service.BancoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bancos")
@RequiredArgsConstructor
@CrossOrigin
public class BancoController {

    private final BancoService service;

    @GetMapping()
    public ResponseEntity get() {
        List<Banco> bancos = service.getBancos();
        return ResponseEntity.ok(bancos.stream().map(BancoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Banco> banco = service.getBancoById(id);
        if (!banco.isPresent()) {
            return new ResponseEntity("Banco não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(banco.map(BancoDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody BancoDTO dto) {
        try {
            Banco banco = converter(dto);
            banco = service.salvar(banco);
            return new ResponseEntity(banco, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody BancoDTO dto) {
        if (!service.getBancoById(id).isPresent()) {
            return new ResponseEntity("Banco não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Banco banco = converter(dto);
            banco.setId(id);
            service.salvar(banco);
            return ResponseEntity.ok(banco);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Banco> banco = service.getBancoById(id);
        if (!banco.isPresent()) {
            return new ResponseEntity("Banco não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(banco.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Banco converter(BancoDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Banco.class);
    }
}
