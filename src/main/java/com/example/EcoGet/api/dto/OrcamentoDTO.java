package com.example.EcoGet.api.dto;

import com.example.EcoGet.Model.entity.Orcamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoDTO {

    private Long id;
    private String nome;
    private Float valorEstimado;
    private Float valorAtual;
    private Long idUsuario;
    private String nomeUsuario;
    private Long idCategoria;
    private String nomeCategoria;
    private Long idPeriodoOrcamental;
    private String tipoPeriodoOrcamental;

    public static OrcamentoDTO create(Orcamento orcamento) {
        ModelMapper modelMapper = new ModelMapper();
        OrcamentoDTO dto = modelMapper.map(orcamento, OrcamentoDTO.class);
        if (orcamento.getUsuario() != null) {
            dto.nomeUsuario = orcamento.getUsuario().getNome();
        }
        if (orcamento.getCategoria() != null) {
            dto.nomeCategoria = orcamento.getCategoria().getNome();
        }
        if (orcamento.getPeriodoOrcamental() != null) {
            dto.idPeriodoOrcamental = orcamento.getPeriodoOrcamental().getId();
            dto.tipoPeriodoOrcamental = orcamento.getPeriodoOrcamental().getTipo();
        }
        return dto;
    }
}
