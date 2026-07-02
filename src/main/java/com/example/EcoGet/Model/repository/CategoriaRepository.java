package com.example.EcoGet.Model.repository;

import com.example.EcoGet.Model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
