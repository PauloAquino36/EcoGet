package com.example.EcoGet.Model.repository;

import com.example.EcoGet.Model.entity.ContaBancaria;
import com.example.EcoGet.Model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

    List<ContaBancaria> findByUsuario(Optional<Usuario> usuario);

    List<ContaBancaria> findByUsuario_Id(Long usuarioId);
}
