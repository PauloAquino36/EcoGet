package com.example.EcoGet.Model.repository;

import com.example.EcoGet.Model.entity.Transacao;
import com.example.EcoGet.Model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuario(Optional<Usuario> usuario);
}
