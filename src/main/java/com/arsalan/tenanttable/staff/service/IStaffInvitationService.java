package com.arsalan.tenanttable.staff.service;

import com.arsalan.tenanttable.staff.dto.AcceptInvitationRequestDto;
import com.arsalan.tenanttable.staff.dto.InviteStaffRequestDto;
import com.arsalan.tenanttable.staff.dto.StaffInvitationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IStaffInvitationService {
    //    INVITATION MANAGEMENT
    StaffInvitationResponseDto invite(InviteStaffRequestDto dto);

    StaffInvitationResponseDto resendInvitation(UUID invitationId);

    StaffInvitationResponseDto cancelInvitation(UUID invitationId);

    StaffInvitationResponseDto getByInvitationId(UUID invitationId);

    Page<StaffInvitationResponseDto> getAllInvitations(Pageable pageable);

    StaffInvitationResponseDto acceptInvitation(AcceptInvitationRequestDto dto);

}
