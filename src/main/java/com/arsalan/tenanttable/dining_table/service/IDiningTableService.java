package com.arsalan.tenanttable.dining_table.service;

import com.arsalan.tenanttable.dining_table.dto.CreateDiningTableRequestDto;
import com.arsalan.tenanttable.dining_table.dto.DiningTableResponseDto;
import com.arsalan.tenanttable.dining_table.dto.UpdateDiningTableRequestDto;
import com.arsalan.tenanttable.dining_table.dto.UpdateDiningTableStatusRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IDiningTableService {
    DiningTableResponseDto create(CreateDiningTableRequestDto dto);

    Page<DiningTableResponseDto> getAll(Pageable pageable);

    DiningTableResponseDto getById(UUID id);

    DiningTableResponseDto update(UUID id, UpdateDiningTableRequestDto dto);

    DiningTableResponseDto updateStatus(UUID id, UpdateDiningTableStatusRequestDto dto);
}
