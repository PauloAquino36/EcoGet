package com.example.EcoGet.Model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Float valorEstimado;
    private Float valorAtual;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Categoria categoria;

    @ManyToOne
    private PeriodoOrcamental periodoOrcamental;
}
