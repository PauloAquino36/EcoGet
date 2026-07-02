package com.example.EcoGet.api.dto;

import com.example.EcoGet.Model.entity.ContaBancaria;
import com.example.EcoGet.Model.entity.Transacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoDTO {

    private Long id;
    private Float valor;
    private LocalDate data;
    private String descricao;
    private boolean recorrente;
    private Long idUsuario;
    private String nomeUsuario;
    private Long idTipoTransacao;
    private String nomeTipoTransacao;
    private Long idCategoria;
    private String nomeCategoria;
    private Long idContaBancaria;
    private String nomeContaBancaria;

    public static TransacaoDTO create(Transacao transacao) {
        ModelMapper modelMapper = new ModelMapper();
        TransacaoDTO dto = modelMapper.map(transacao, TransacaoDTO.class);
        if (transacao.getUsuario() != null) {
            dto.nomeUsuario = transacao.getUsuario().getNome();
        }
        if (transacao.getTipoTransacao() != null) {
            dto.nomeTipoTransacao = transacao.getTipoTransacao().getNome();
        }
        if (transacao.getCategoria() != null) {
            dto.nomeCategoria = transacao.getCategoria().getNome();
        }
        if (transacao.getContaBancaria() != null) {
            ContaBancaria cb = transacao.getContaBancaria();
            dto.nomeContaBancaria = cb.getConta()
                + (cb.getBanco() != null ? " · " + cb.getBanco().getNome() : "");
        }
        return dto;
    }
}
