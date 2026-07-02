package com.example.EcoGet.api.dto;

import com.example.EcoGet.Model.entity.PeriodoOrcamental;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoOrcamentalDTO {

    private Long id;
    private String tipo;

    public static PeriodoOrcamentalDTO create(PeriodoOrcamental periodoOrcamental) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(periodoOrcamental, PeriodoOrcamentalDTO.class);
    }
}
