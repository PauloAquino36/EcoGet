package com.example.EcoGet.api.dto;

import com.example.EcoGet.Model.entity.Banco;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BancoDTO {

    private Long id;
    private String nome;
    private String codigo;

    public static BancoDTO create(Banco banco) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(banco, BancoDTO.class);
    }
}
