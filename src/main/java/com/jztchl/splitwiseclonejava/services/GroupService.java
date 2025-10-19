package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.CreateGroupDto;
import com.jztchl.splitwiseclonejava.dtos.GroupDetailsDto;
import com.jztchl.splitwiseclonejava.dtos.GroupListDto;
import com.jztchl.splitwiseclonejava.dtos.GroupMembersDto;
import com.jztchl.splitwiseclonejava.models.GroupMembers;
import com.jztchl.splitwiseclonejava.models.Groups;
import com.jztchl.splitwiseclonejava.models.Users;
import com.jztchl.splitwiseclonejava.repos.GroupMembersRepository;
import com.jztchl.splitwiseclonejava.repos.GroupRepository;
import com.jztchl.splitwiseclonejava.repos.UserRepository;
import com.jztchl.splitwiseclonejava.utility.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final EmailService emailService;

    public GroupService(GroupRepository groupRepository, JwtService jwtService, UserRepository userRepository,
                        GroupMembersRepository groupMembersRepository, EmailService emailService) {
        this.groupRepository = groupRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Groups createGroup(CreateGroupDto group) {
        Groups newGroup = new Groups();
        newGroup.setGroupName(group.getGroupName());
        newGroup.setDescription(group.getGroupDescription());
        newGroup.setCreatedBy(jwtService.getCurrentUser());
        newGroup = groupRepository.save(newGroup);
        HashSet<GroupMembers> members = new HashSet<>();
        if (!(group.getMembers() == null)) {
            for (String email : group.getMembers()) {
                Users user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException(String.format("User not found email: %s", email)));
                GroupMembers groupMember = new GroupMembers();
                groupMember.setGroupId(newGroup);
                groupMember.setUserId(user);
                members.add(groupMember);
            }

        }
        GroupMembers groupMember = new GroupMembers();
        groupMember.setGroupId(newGroup);
        groupMember.setUserId(jwtService.getCurrentUser());
        members.add(groupMember);
        Groups finalNewGroup = newGroup;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    emailService.addedToGroupNotification(finalNewGroup);
                    System.out.println("Group created successfully");
                } catch (Exception e) {
                    System.err.println("Error in post-commit processing: " + e.getMessage());
                }
            }
        });
        groupMembersRepository.saveAll(members);
        return newGroup;
    }

    public GroupDetailsDto getGroupById(Long id) {
        Users currentUser = jwtService.getCurrentUser();
        Groups group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Group not found id: %d", id)));
        if (!groupMembersRepository.existsByGroupIdAndUserId(group, currentUser)) {
            throw new RuntimeException("You are not a member of this group");
        }
        GroupDetailsDto groupDetailsDto = new GroupDetailsDto();
        groupDetailsDto.setId(group.getId());
        groupDetailsDto.setName(group.getGroupName());
        groupDetailsDto.setDescription(group.getDescription());
        List<GroupMembersDto> membersDto = new ArrayList<>();
        for (GroupMembers member : group.getMembers()) {
            GroupMembersDto groupMembersDto = new GroupMembersDto();
            groupMembersDto.setId(Long.valueOf(member.getUserId().getId()));
            groupMembersDto.setName(member.getUserId().getName());
            groupMembersDto.setDescription(member.getUserId().getEmail());
            membersDto.add(groupMembersDto);

        }
        groupDetailsDto.setMembers(membersDto);
        return groupDetailsDto;
    }

    public List<GroupListDto> getAllGroups() {
        Users currentUser = jwtService.getCurrentUser();
        return groupRepository.findAllGroupListsByMember(currentUser);
    }

    public void addMemberToGroup(Long groupId, List<String> members) {
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException(String.format("Group not found id: %d", groupId)));
        if (!jwtService.getCurrentUser().equals(group.getCreatedBy())) {
            throw new RuntimeException("Only the creator of the group can add members");
        }
        if (!(members == null)) {
            HashSet<GroupMembers> groupMembers = new HashSet<>();
            for (String email : members) {
                Users newUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException(String.format("User:%s not found ", email)));

                if (groupMembersRepository.existsByGroupIdAndUserId(group, newUser)) {
                    throw new RuntimeException("User is already present");
                }
                GroupMembers groupMember = new GroupMembers();
                groupMember.setGroupId(group);
                groupMember.setUserId(newUser);
                groupMembers.add(groupMember);
            }
            groupMembersRepository.saveAll(groupMembers);

        }

    }

}
