package com.arsalan.tenanttable.dining_table.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.dining_table.dto.CreateDiningTableRequestDto;
import com.arsalan.tenanttable.dining_table.dto.DiningTableResponseDto;
import com.arsalan.tenanttable.dining_table.dto.UpdateDiningTableRequestDto;
import com.arsalan.tenanttable.dining_table.dto.UpdateDiningTableStatusRequestDto;
import com.arsalan.tenanttable.dining_table.service.IDiningTableService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dining-tables")
public class DiningTableController {
    private final IDiningTableService diningTableService;

    @PostMapping
    public ResponseEntity<ApiResponse<DiningTableResponseDto>> create(
            @Valid @RequestBody CreateDiningTableRequestDto dto,
            HttpServletRequest request
    ) {

        DiningTableResponseDto diningTable = diningTableService.create(dto);

        ApiResponse<DiningTableResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Dining Table created successfully",
                diningTable,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<Page<DiningTableResponseDto>>> getAll(
            Pageable pageable,
            HttpServletRequest request
    ) {
        Page<DiningTableResponseDto> tables = diningTableService.getAll(pageable);

        ApiResponse<Page<DiningTableResponseDto>> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Dining Tables fetched successfully.",
                        tables,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiningTableResponseDto>> getById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        DiningTableResponseDto diningTable = diningTableService.getById(id);

        ApiResponse<DiningTableResponseDto> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Dining Table fetched successfully.",
                        diningTable,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DiningTableResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDiningTableRequestDto dto,
            HttpServletRequest request
    ) {

        DiningTableResponseDto diningTable = diningTableService.update(id, dto);


        ApiResponse<DiningTableResponseDto> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Dining Table updated successfully.",
                        diningTable,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DiningTableResponseDto>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDiningTableStatusRequestDto dto,
            HttpServletRequest request
    ) {

        DiningTableResponseDto diningTable = diningTableService.updateStatus(id, dto);


        ApiResponse<DiningTableResponseDto> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Dining Table updated successfully.",
                        diningTable,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

}
