package com.arsalan.tenanttable.category.service;

import com.arsalan.tenanttable.category.dto.CategoryResponseDto;
import com.arsalan.tenanttable.category.dto.CreateCategoryRequestDto;
import com.arsalan.tenanttable.category.dto.UpdateCategoryRequestDto;
import com.arsalan.tenanttable.category.entity.Category;
import com.arsalan.tenanttable.category.mapper.CategoryMapper;
import com.arsalan.tenanttable.category.repository.CategoryRepository;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.ResourceAlreadyExistsException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ICurrentUserUtilService currentUserUtilService;

    @Override
    public CategoryResponseDto create(CreateCategoryRequestDto dto) {
        Tenant currentTenant = currentUserUtilService.getCurrentTenant();
        String categoryName = dto.getName().trim();

        if (categoryRepository.existsByTenantIdAndNameIgnoreCase(currentTenant.getId(), categoryName))
            throw new ResourceAlreadyExistsException("Category '" + dto.getName() + "' already exists");

        Category category = Category.builder()
                .name(categoryName)
                .description(dto.getDescription())
                .tenant(currentTenant)
                .build();

        Category savedCategory = categoryRepository.save(category);

        log.info(
                "Category '{}' created in tenant '{}'.",
                savedCategory.getName(),
                currentTenant.getName()
        );

        return CategoryMapper.toDto(savedCategory);
    }

    @Override
    public Page<CategoryResponseDto> getAll(Pageable pageable) {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();
        Page<Category> categories = categoryRepository
                .findAllByTenantId(tenantId, pageable);

        log.info(
                "Retrieved {} categories for tenant '{}'.",
                categories.getNumberOfElements(),
                tenantId
        );

        return categories.map(CategoryMapper::toDto);
    }


    @Override
    public CategoryResponseDto getById(UUID id) {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        Category category = categoryRepository
                .findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id '" + id + "' not found."
                        ));

        log.info("Category '{}' fetched", category.getName());
        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto update(UUID id, UpdateCategoryRequestDto dto) {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        Category category = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id '" + id + "' not found."
                        ));

        if (dto.getName() != null) {
            String newName = dto.getName().trim();

            if (!category.getName().equalsIgnoreCase(newName) &&
                    categoryRepository.existsByTenantIdAndNameIgnoreCase(tenantId, newName)) {
                throw new ResourceAlreadyExistsException("Category '" + newName + "' already exists.");
            }

            category.setName(dto.getName().trim());
        }

        if (dto.getDescription() != null)
            category.setDescription(dto.getDescription().trim());

        Category updatedCategory = categoryRepository.save(category);
        log.info(
                "Category '{}' updated in tenant '{}'.",
                updatedCategory.getName(),
                tenantId
        );

        return CategoryMapper.toDto(updatedCategory);
    }

    @Override
    public void delete(UUID id) {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        Category category = categoryRepository
                .findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id '" + id + "' not found."
                        ));

        categoryRepository.delete(category);

        log.info("Category '{}' deleted.", category.getName());
    }
}
