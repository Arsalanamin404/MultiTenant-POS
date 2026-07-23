package com.arsalan.tenanttable.staff.service;

import com.arsalan.tenanttable.staff.dto.StaffResponseDto;
import com.arsalan.tenanttable.staff.dto.UpdateStaffRoleRequestDto;
import com.arsalan.tenanttable.staff.dto.UpdateStaffStatusRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IStaffService {
    //    STAFF MANAGEMENT
    Page<StaffResponseDto> getAllStaff(Pageable pageable);

    StaffResponseDto getStaffById(UUID staffId);

    StaffResponseDto updateRole(UUID staffId, UpdateStaffRoleRequestDto dto);

    StaffResponseDto updateStatus(UUID staffId, UpdateStaffStatusRequestDto dto);
    
}
