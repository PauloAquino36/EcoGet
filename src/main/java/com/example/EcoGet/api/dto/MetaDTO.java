package com.example.EcoGet.api.dto;

import com.example.EcoGet.Model.entity.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDTO {

    private Long id;
    private String nome;
    private Float valor;
    private Float progresso;
    private String descricao;
    private LocalDate inicio;
    private LocalDate fim;
    private Integer diasCorridos;
    private Long idUsuario;
    private String nomeUsuario;
    private Long idContaBancaria;
    private String nomeContaBancaria;

    public static MetaDTO create(Meta meta) {
        ModelMapper modelMapper = new ModelMapper();
        MetaDTO dto = modelMapper.map(meta, MetaDTO.class);
        if (meta.getUsuario() != null) {
            dto.nomeUsuario = meta.getUsuario().getNome();
        }
        if (meta.getContaBancaria() != null) {
            dto.idContaBancaria = meta.getContaBancaria().getId();
            dto.nomeContaBancaria = meta.getContaBancaria().getConta();
        }
        return dto;
    }
}
