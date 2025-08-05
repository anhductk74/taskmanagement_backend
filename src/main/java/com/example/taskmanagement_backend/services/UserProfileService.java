package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.dtos.UserProfileDto.CreateUserProfileRequestDto;
import com.example.taskmanagement_backend.dtos.UserProfileDto.UpdateUserProfileRequestDto;
import com.example.taskmanagement_backend.dtos.UserProfileDto.UserProfileResponseDto;
import com.example.taskmanagement_backend.entities.Project;
import com.example.taskmanagement_backend.entities.User;
import com.example.taskmanagement_backend.entities.UserProfile;
import com.example.taskmanagement_backend.repositories.UserJpaRepository;
import com.example.taskmanagement_backend.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class UserProfileService {
    @Autowired
    private final UserProfileRepository userProfileRepository;
    private final UserJpaRepository userJpaRepository;

    public UserProfileResponseDto getUserProfile(Long id) {
        return userProfileRepository.findByUserId(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    public UserProfileResponseDto updateUserProfile(Long uid, UpdateUserProfileRequestDto dto) {
        UserProfile userProfile = convertToEntity(getUserProfile(uid));
        System.out.println("userProfile: "+userProfile);
                if(dto.getFirstName() != null ) userProfile.setFirstName(dto.getFirstName());
                if(dto.getLastName() != null ) userProfile.setLastName(dto.getLastName());
                if(dto.getAvtUrl() != null ) userProfile.setAvtUrl(dto.getAvtUrl());

                User user = getUser(uid);
                if(user.isFirstLogin()) {
                    user.setFirstLogin(false);
                    userJpaRepository.save(user);
                }
        return convertToDto(userProfileRepository.save(userProfile));
    }

    public UserProfileResponseDto convertToDto(UserProfile userProfile) {
        UserProfileResponseDto userProfileResponseDto = new UserProfileResponseDto();
        userProfileResponseDto.setId(userProfile.getId());
        userProfileResponseDto.setFirstName(userProfile.getFirstName());
        userProfileResponseDto.setLastName(userProfile.getLastName());
        userProfileResponseDto.setStatus(userProfile.getStatus());
        userProfileResponseDto.setAvtUrl(userProfile.getAvtUrl());
        userProfileResponseDto.setUserId(userProfile.getUser() != null ? userProfile.getUser().getId() : null);
        return userProfileResponseDto;
    }

    public UserProfile  convertToEntity(UserProfileResponseDto dto) {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(dto.getId());
        userProfile.setFirstName(dto.getFirstName());
        userProfile.setLastName(dto.getLastName());
        userProfile.setStatus(dto.getStatus());
        userProfile.setAvtUrl(dto.getAvtUrl());
        userProfile.setUser(dto.getUserId() != null ? getUser(dto.getUserId()) : null);
        return userProfile;
    }

    private User getUser(Long id) {
        return id != null ? userJpaRepository.findById(id).orElse(null) : null;
    }
}
