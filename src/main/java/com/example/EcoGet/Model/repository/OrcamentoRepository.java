package com.example.EcoGet.Model.repository;

import com.example.EcoGet.Model.entity.Orcamento;
import com.example.EcoGet.Model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    List<Orcamento> findByUsuario(Optional<Usuario> usuario);

    List<Orcamento> findByCategoria_Id(Long categoriaId);

    List<Orcamento> findByUsuario_IdAndCategoria_Id(Long usuarioId, Long categoriaId);
}
