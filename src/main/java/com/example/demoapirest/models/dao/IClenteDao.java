package com.example.demoapirest.models.dao;

import com.example.demoapirest.models.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface IClenteDao extends CrudRepository<Cliente, Long> {
}
