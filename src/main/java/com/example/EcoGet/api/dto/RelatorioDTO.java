package com.example.EcoGet.api.dto;

import com.example.EcoGet.Model.entity.Relatorio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDTO {

    private Long id;
    private String tipo;
    private LocalDate dataGeracao;
    private Long idUsuario;
    private String nomeUsuario;

    public static RelatorioDTO create(Relatorio relatorio) {
        ModelMapper modelMapper = new ModelMapper();
        RelatorioDTO dto = modelMapper.map(relatorio, RelatorioDTO.class);
        if (relatorio.getUsuario() != null) {
            dto.nomeUsuario = relatorio.getUsuario().getNome();
        }
        return dto;
    }
}
