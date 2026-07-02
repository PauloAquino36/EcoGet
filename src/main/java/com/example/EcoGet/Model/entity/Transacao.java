package com.example.EcoGet.Model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float valor;
    private LocalDate data;
    private String descricao;
    private boolean recorrente;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private TipoTransacao tipoTransacao;

    @ManyToOne
    private Categoria categoria;

    @ManyToOne
    private ContaBancaria contaBancaria;
}
