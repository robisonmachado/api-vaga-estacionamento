package com.api.controleestacionamento.repositories;

import com.api.controleestacionamento.models.VagaEstacionamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VagaEstacionamentoRepository extends JpaRepository<VagaEstacionamentoModel, UUID> {
    boolean existsByPlacaVeiculo(String placaVeiculo);
    boolean existsByNumeroVaga(String numeroVaga);
    boolean existsByApartamentoAndBloco(String apartamento, String bloco);
}
