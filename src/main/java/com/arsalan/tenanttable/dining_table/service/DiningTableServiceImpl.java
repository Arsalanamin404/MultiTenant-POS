package com.arsalan.tenanttable.dining_table.service;

import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.dining_table.dto.CreateDiningTableRequestDto;
import com.arsalan.tenanttable.dining_table.dto.DiningTableResponseDto;
import com.arsalan.tenanttable.dining_table.dto.UpdateDiningTableRequestDto;
import com.arsalan.tenanttable.dining_table.dto.UpdateDiningTableStatusRequestDto;
import com.arsalan.tenanttable.dining_table.entity.DiningTable;
import com.arsalan.tenanttable.dining_table.enums.DiningTableStatus;
import com.arsalan.tenanttable.dining_table.mapper.DiningTableMapper;
import com.arsalan.tenanttable.dining_table.repository.DiningTableRepository;
import com.arsalan.tenanttable.exception.ResourceAlreadyExistsException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiningTableServiceImpl implements IDiningTableService {
    private final ICurrentUserUtilService currentUserUtilService;
    private final DiningTableRepository diningTableRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public DiningTableResponseDto create(CreateDiningTableRequestDto dto) {
        UUID userId = currentUserUtilService.getCurrentUserId();
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Tenant currentTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        String normalizedTableNumber = dto.getTableNumber().trim().toUpperCase(Locale.ROOT);

        if (diningTableRepository.existsByTableNumberAndTenant(
                normalizedTableNumber,
                currentTenant)) {
            log.warn("Table with number = {} already exists for tenant id = {}.",
                    normalizedTableNumber,
                    currentTenant.getId()
            );
            throw new ResourceAlreadyExistsException(
                    "Table with number '" + normalizedTableNumber + "' already exists."
            );
        }

        DiningTable diningTable = DiningTable.builder()
                .tableNumber(normalizedTableNumber)
                .capacity(dto.getCapacity())
                .description(dto.getDescription())
                .status(DiningTableStatus.AVAILABLE)
                .tenant(currentTenant)
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();

        DiningTable savedTable = diningTableRepository.save(diningTable);

        log.info(
                "Dining table {} created successfully for tenant {} by user {}.",
                savedTable.getTableNumber(),
                currentTenant.getId(),
                currentUser.getId()
        );

        return DiningTableMapper.toDto(savedTable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiningTableResponseDto> getAll(Pageable pageable) {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        Tenant currentTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Page<DiningTable> tables = diningTableRepository.findAllByTenant(currentTenant, pageable);
        log.info(
                "Retrieved {} dining tables for tenant '{}'.",
                tables.getNumberOfElements(),
                currentTenant.getId()
        );

        return tables.map(DiningTableMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public DiningTableResponseDto getById(UUID id) {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        Tenant currentTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        DiningTable diningTable = diningTableRepository
                .findByIdAndTenant(id, currentTenant)
                .orElseThrow(() -> new ResourceNotFoundException("Dining table with id '" + id + "' not found."));

        log.info("Retrieved dining table for tenant '{}'", currentTenant.getId());

        return DiningTableMapper.toDto(diningTable);
    }

    @Override
    @Transactional
    public DiningTableResponseDto update(UUID id, UpdateDiningTableRequestDto dto) {
        UUID userId = currentUserUtilService.getCurrentUserId();
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Tenant currentTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        DiningTable diningTable = diningTableRepository.findByIdAndTenant(id, currentTenant)
                .orElseThrow(() -> new ResourceNotFoundException("Dining table with id '" + id + "' not found."));

        String normalizedTableNumber = dto.getTableNumber().trim().toUpperCase(Locale.ROOT);

        if (!diningTable.getTableNumber().equals(normalizedTableNumber)
                && diningTableRepository.existsByTableNumberAndTenant(normalizedTableNumber, currentTenant)
        ) {

            log.warn(
                    "Table with number {} already exists for tenant {}.",
                    normalizedTableNumber,
                    currentTenant.getId()
            );

            throw new ResourceAlreadyExistsException(
                    "Table with number '" + normalizedTableNumber + "' already exists."
            );
        }

        diningTable.setTableNumber(normalizedTableNumber);
        diningTable.setCapacity(dto.getCapacity());
        diningTable.setDescription(dto.getDescription());
        diningTable.setUpdatedBy(currentUser);

        DiningTable updatedTable = diningTableRepository.save(diningTable);

        log.info(
                "Dining table {} updated successfully for tenant {}.",
                updatedTable.getTableNumber(),
                currentTenant.getId()
        );

        return DiningTableMapper.toDto(updatedTable);
    }

    @Override
    @Transactional
    public DiningTableResponseDto updateStatus(UUID id, UpdateDiningTableStatusRequestDto dto) {
        UUID userId = currentUserUtilService.getCurrentUserId();
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Tenant currentTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        DiningTable table = diningTableRepository.findByIdAndTenant(id,currentTenant)
                .orElseThrow(()->new ResourceNotFoundException("Dining table with id '" + id + "' not found."));

        table.setStatus(dto.getStatus());
        table.setUpdatedBy(currentUser);

        DiningTable updatedTable = diningTableRepository.save(table);

        log.info(
                "Dining table {} status changed to {} for tenant {} by user {}.",
                updatedTable.getTableNumber(),
                updatedTable.getStatus(),
                currentTenant.getId(),
                currentUser.getId()
        );

        return DiningTableMapper.toDto(updatedTable);
    }
}
