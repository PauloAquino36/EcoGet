package com.example.EcoGet.api.dto;

import com.example.EcoGet.Model.entity.Periodo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoDTO {

    private Long id;
    private LocalDate inicial;
    private Long idPeriodoOrcamental;

    // Campos somente de resposta (calculados automaticamente)
    private LocalDate fim;
    private String tipoPeriodoOrcamental;

    public static PeriodoDTO create(Periodo periodo) {
        ModelMapper modelMapper = new ModelMapper();
        PeriodoDTO dto = modelMapper.map(periodo, PeriodoDTO.class);
        if (periodo.getPeriodoOrcamental() != null) {
            dto.idPeriodoOrcamental = periodo.getPeriodoOrcamental().getId();
            dto.tipoPeriodoOrcamental = periodo.getPeriodoOrcamental().getTipo();
        }
        return dto;
    }
}
