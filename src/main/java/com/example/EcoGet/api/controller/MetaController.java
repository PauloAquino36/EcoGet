package com.example.EcoGet.api.controller;

import com.example.EcoGet.api.dto.MetaDTO;
import com.example.EcoGet.exeption.RegraNegocioException;
import com.example.EcoGet.Model.entity.ContaBancaria;
import com.example.EcoGet.Model.entity.Meta;
import com.example.EcoGet.Model.entity.Usuario;
import com.example.EcoGet.service.ContaBancariaService;
import com.example.EcoGet.service.MetaService;
import com.example.EcoGet.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/metas")
@RequiredArgsConstructor
@CrossOrigin
public class MetaController {

    private final MetaService service;
    private final UsuarioService usuarioService;
    private final ContaBancariaService contaBancariaService;

    @GetMapping()
    public ResponseEntity get() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuario = usuarioService.getUsuarioByEmail(email);
        List<Meta> metas = service.getMetasByUsuario(usuario);
        return ResponseEntity.ok(metas.stream().map(MetaDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Meta> meta = service.getMetaById(id);
        if (!meta.isPresent()) {
            return new ResponseEntity("Meta não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(meta.map(MetaDTO::create));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity getByUsuario(@PathVariable("idUsuario") Long idUsuario) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(idUsuario);
        if (!usuario.isPresent()) {
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        List<Meta> metas = service.getMetasByUsuario(usuario);
        return ResponseEntity.ok(metas.stream().map(MetaDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody MetaDTO dto) {
        try {
            Meta meta = converter(dto);
            meta = service.salvar(meta);
            return new ResponseEntity(MetaDTO.create(meta), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody MetaDTO dto) {
        if (!service.getMetaById(id).isPresent()) {
            return new ResponseEntity("Meta não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            Meta meta = converter(dto);
            meta.setId(id);
            service.salvar(meta);
            return ResponseEntity.ok(MetaDTO.create(meta));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Meta> meta = service.getMetaById(id);
        if (!meta.isPresent()) {
            return new ResponseEntity("Meta não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(meta.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("{id}/depositar")
    public ResponseEntity depositar(@PathVariable("id") Long id, @RequestBody Map<String, Float> body) {
        try {
            Float valor = body.get("valor");
            Meta meta = service.depositar(id, valor);
            return ResponseEntity.ok(MetaDTO.create(meta));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("{id}/sacar")
    public ResponseEntity sacar(@PathVariable("id") Long id, @RequestBody Map<String, Float> body) {
        try {
            Float valor = body.get("valor");
            Meta meta = service.sacar(id, valor);
            return ResponseEntity.ok(MetaDTO.create(meta));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Meta converter(MetaDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Meta meta = modelMapper.map(dto, Meta.class);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        usuarioService.getUsuarioByEmail(email).ifPresent(meta::setUsuario);
        if (dto.getIdContaBancaria() != null) {
            Optional<ContaBancaria> conta = contaBancariaService.getContaBancariaById(dto.getIdContaBancaria());
            conta.ifPresent(c -> {
                meta.setContaBancaria(c);
                meta.setProgresso(c.getSaldo() != null ? c.getSaldo() : 0f);
            });
        }
        return meta;
    }
}
