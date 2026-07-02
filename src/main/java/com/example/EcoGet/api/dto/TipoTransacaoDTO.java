package com.example.EcoGet.api.dto;

import com.example.EcoGet.Model.entity.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoTransacaoDTO {

    private Long id;
    private String nome;
    private Boolean credito;

    public static TipoTransacaoDTO create(TipoTransacao tipoTransacao) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(tipoTransacao, TipoTransacaoDTO.class);
    }
}
