package com.example.EcoGet.api.dto;

import com.example.EcoGet.Model.entity.ContaBancaria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaBancariaDTO {

    private Long id;
    private String agencia;
    private String conta;
    private Float saldo;
    private Long idBanco;
    private String nomeBanco;
    private Long idUsuario;
    private String nomeUsuario;

    public static ContaBancariaDTO create(ContaBancaria contaBancaria) {
        ModelMapper modelMapper = new ModelMapper();
        ContaBancariaDTO dto = modelMapper.map(contaBancaria, ContaBancariaDTO.class);
        if (contaBancaria.getBanco() != null) {
            dto.nomeBanco = contaBancaria.getBanco().getNome();
        }
        if (contaBancaria.getUsuario() != null) {
            dto.nomeUsuario = contaBancaria.getUsuario().getNome();
        }
        return dto;
    }
}
