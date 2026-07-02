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
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Float valor;
    private Float progresso;
    private String descricao;
    private LocalDate inicio;
    private LocalDate fim;
    private Integer diasCorridos;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private ContaBancaria contaBancaria;
}
