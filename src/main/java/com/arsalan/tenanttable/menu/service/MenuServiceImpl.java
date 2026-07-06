package com.arsalan.tenanttable.menu.service;

import com.arsalan.tenanttable.category.entity.Category;
import com.arsalan.tenanttable.category.repository.CategoryRepository;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.ResourceAlreadyExistsException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.menu.dto.CreateMenuItemRequestDto;
import com.arsalan.tenanttable.menu.dto.MenuItemResponseDto;
import com.arsalan.tenanttable.menu.dto.UpdateMenuItemRequestDto;
import com.arsalan.tenanttable.menu.entity.MenuItem;
import com.arsalan.tenanttable.menu.mapper.MenuItemMapper;
import com.arsalan.tenanttable.menu.repository.MenuRepository;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;
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
public class MenuServiceImpl implements IMenuService {
    private final MenuRepository menuRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final CategoryRepository categoryRepository;

    private MenuItem getMenuItemOrThrow(UUID id, UUID tenantId) {
        return menuRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu item with id '" + id + "' not found."
                        ));
    }

    private Category getCategoryOrThrow(UUID categoryId, UUID tenantId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id '" + categoryId + "' not found."
                        ));

        if (!category.getTenant().getId().equals(tenantId)) {
            throw new ResourceNotFoundException(
                    "Category does not belong to your tenant."
            );
        }

        return category;
    }

    @Override
    @Transactional
    public MenuItemResponseDto createMenuItem(CreateMenuItemRequestDto dto) {
        User currentUser = currentUserUtilService.getCurrentUser();
        Tenant currentTenant = currentUserUtilService.getCurrentTenant();

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category with id '" + dto.getCategoryId() + "' not found"));

        if (!category.getTenant().getId().equals(currentTenant.getId())) {
            throw new ResourceNotFoundException("Category does not belong to your tenant");
        }

        if (menuRepository.existsByNameIgnoreCaseAndTenantId(dto.getName().trim(), currentTenant.getId())) {
            throw new ResourceAlreadyExistsException("Menu item already exists");
        }

        log.info(
                "Creating menu item '{}' for tenant '{}'",
                dto.getName(),
                currentTenant.getId()
        );

        MenuItem menuItem = MenuItem.builder()
                .name(dto.getName().trim())
                .description(
                        dto.getDescription() != null
                                ? dto.getDescription().trim() : null)
                .price(dto.getPrice())
                .available(dto.getAvailable())
                .category(category)
                .tenant(currentTenant)
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();

        MenuItem savedMenuItem = menuRepository.save(menuItem);
        log.info(
                "Menu item '{}' created successfully with id {}",
                savedMenuItem.getName(),
                savedMenuItem.getId()
        );
        return MenuItemMapper.toDto(savedMenuItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuItemResponseDto> getAll(Pageable pageable) {
        Tenant currentTenant = currentUserUtilService.getCurrentTenant();

        return menuRepository
                .findAllByTenantId(currentTenant.getId(), pageable)
                .map(MenuItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponseDto getById(UUID id) {

        Tenant currentTenant = currentUserUtilService.getCurrentTenant();

        MenuItem menuItem = menuRepository
                .findByIdAndTenantId(id, currentTenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item with id '" + id + "' not found"));

        return MenuItemMapper.toDto(menuItem);
    }

    @Override
    @Transactional
    public MenuItemResponseDto updateMenuItem(UUID id, UpdateMenuItemRequestDto dto) {
        User currentUser = currentUserUtilService.getCurrentUser();
        Tenant currentTenant = currentUserUtilService.getCurrentTenant();

        MenuItem menuItem = getMenuItemOrThrow(id, currentTenant.getId());

        Category category =  getCategoryOrThrow(dto.getCategoryId(),currentTenant.getId());

        String menuName = dto.getName().trim();

        if (!menuItem.getName().equalsIgnoreCase(menuName)
                && menuRepository.existsByNameIgnoreCaseAndTenantId(
                menuName, currentTenant.getId())) {

            throw new ResourceAlreadyExistsException("Menu item '" + menuName + "' already exists.");
        }

        log.info(
                "Updating menu item '{}' for tenant '{}'",
                menuItem.getId(),
                currentTenant.getId()
        );

        menuItem.setName(menuName);
        menuItem.setDescription(
                dto.getDescription() != null
                        ? dto.getDescription().trim()
                        : null
        );
        menuItem.setPrice(dto.getPrice());
        menuItem.setAvailable(dto.getAvailable());
        menuItem.setCategory(category);
        menuItem.setUpdatedBy(currentUser);

        MenuItem updated = menuRepository.save(menuItem);

        return MenuItemMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void updateMenuItemAvailability(UUID id, boolean available) {
        User currentUser = currentUserUtilService.getCurrentUser();
        Tenant currentTenant = currentUserUtilService.getCurrentTenant();

        MenuItem menuItem = getMenuItemOrThrow(id,currentTenant.getId());

        log.info("Updating availability of menu item '{}' to {}", id, available);

        menuItem.setAvailable(available);
        menuItem.setUpdatedBy(currentUser);

        menuRepository.save(menuItem);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Tenant currentTenant = currentUserUtilService.getCurrentTenant();

        MenuItem menuItem = getMenuItemOrThrow(id,currentTenant.getId());

        log.info("Deleting menu item '{}'", id);

        menuRepository.delete(menuItem);
    }
}
