package com.example.EcoGet.Model.repository;

import com.example.EcoGet.Model.entity.Meta;
import com.example.EcoGet.Model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetaRepository extends JpaRepository<Meta, Long> {

    List<Meta> findByUsuario(Optional<Usuario> usuario);
}
