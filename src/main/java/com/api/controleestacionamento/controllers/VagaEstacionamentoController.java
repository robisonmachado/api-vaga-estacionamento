package com.api.controleestacionamento.controllers;

import com.api.controleestacionamento.dtos.VagaEstacionamentoDto;
import com.api.controleestacionamento.models.VagaEstacionamentoModel;
import com.api.controleestacionamento.services.VagaEstacionamentoService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("vaga-estacionamento")
public class VagaEstacionamentoController {

    final VagaEstacionamentoService vagaEstacionamentoService;

    public VagaEstacionamentoController(VagaEstacionamentoService vagaEstacionamentoService) {
        this.vagaEstacionamentoService = vagaEstacionamentoService;
    }

    @PostMapping
    public ResponseEntity<Object> saveVagaEstacionamento(@RequestBody @Valid VagaEstacionamentoDto vagaEstacionamentoDto){

        if(vagaEstacionamentoService.existsByPlacaVeiculo(vagaEstacionamentoDto.getPlacaVeiculo())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Placa do Veículo já está sendo usada em outra vaga de estacionamento!");
        }
        if(vagaEstacionamentoService.existsByNumeroVaga(vagaEstacionamentoDto.getNumeroVaga())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Número da vaga já existe, vaga de estacionamento está ocupada!");
        }
        if(vagaEstacionamentoService.existsByApartamentoAndBloco(vagaEstacionamentoDto.getApartamento(), vagaEstacionamentoDto.getBloco())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Vaga de estacionamento já registrada para esse apartamento e bloco!");
        }

        var vagaEstacionamentoModel = new VagaEstacionamentoModel();
        BeanUtils.copyProperties(vagaEstacionamentoDto, vagaEstacionamentoModel);
        vagaEstacionamentoModel.setDataRegistro(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(vagaEstacionamentoService.save(vagaEstacionamentoModel));
    }


    @GetMapping
    public ResponseEntity<Page<VagaEstacionamentoModel>> getAllVagasEstacionamentos(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(vagaEstacionamentoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneVagaEstacionamento(@PathVariable(value = "id") UUID id){
        Optional<VagaEstacionamentoModel> vagaEstacionamentoModelOptional = vagaEstacionamentoService.findById(id);
        if (!vagaEstacionamentoModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga de estacionamento não encontrada.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(vagaEstacionamentoModelOptional.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteVagaEstacionamento(@PathVariable(value = "id") UUID id){
        Optional<VagaEstacionamentoModel> vagaEstacionamentoModelOptional = vagaEstacionamentoService.findById(id);
        if (!vagaEstacionamentoModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga de estacionamento não encontrada.");
        }
        vagaEstacionamentoService.delete(vagaEstacionamentoModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Vaga de estacionamento deletada com sucesso.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateVagaEstacionamento(@PathVariable(value = "id") UUID id,
                                                           @RequestBody @Valid VagaEstacionamentoDto vagaEstacionamentoDto){
        Optional<VagaEstacionamentoModel> vagaEstacionamentoModelOptional = vagaEstacionamentoService.findById(id);
        if (!vagaEstacionamentoModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga de estacionamento não encontrada.");
        }

        var vagaEstacionamentoModel = new VagaEstacionamentoModel();
        BeanUtils.copyProperties(vagaEstacionamentoDto, vagaEstacionamentoModel);
        vagaEstacionamentoModel.setId(vagaEstacionamentoModelOptional.get().getId());
        vagaEstacionamentoModel.setDataRegistro(vagaEstacionamentoModelOptional.get().getDataRegistro());

        return ResponseEntity.status(HttpStatus.OK).body(vagaEstacionamentoService.save(vagaEstacionamentoModel));
    }



}
