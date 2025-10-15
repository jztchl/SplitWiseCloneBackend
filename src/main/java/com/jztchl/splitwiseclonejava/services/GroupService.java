package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.CreateGroupDto;
import com.jztchl.splitwiseclonejava.dtos.GroupListDto;
import com.jztchl.splitwiseclonejava.models.GroupMembers;
import com.jztchl.splitwiseclonejava.models.Groups;
import com.jztchl.splitwiseclonejava.models.Users;
import com.jztchl.splitwiseclonejava.repos.GroupMembersRepository;
import com.jztchl.splitwiseclonejava.repos.GroupRepository;
import com.jztchl.splitwiseclonejava.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final GroupMembersRepository groupMembersRepository;
    @Autowired
    public GroupService(GroupRepository groupRepository, JwtService jwtService, UserRepository userRepository, GroupMembersRepository groupMembersRepository) {
        this.groupRepository = groupRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.groupMembersRepository = groupMembersRepository;
    }

    @Transactional
    public Groups createGroup(CreateGroupDto group) {
        Groups newGroup = new Groups();
        newGroup.setGroupName(group.getGroupName());
        newGroup.setDescription(group.getGroupDescription());
        newGroup.setCreatedBy(jwtService.getCurrentUser());
        newGroup=groupRepository.save(newGroup);
        HashSet<GroupMembers>  members = new HashSet<>();

        for (String email : group.getMembers()) {
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(()->new RuntimeException(String.format("User not found email: %s", email)));
            GroupMembers groupMember= new GroupMembers();
            groupMember.setGroupId(newGroup);
            groupMember.setUserId(user);
            members.add(groupMember);
        }
        if (!members.isEmpty()) {
            groupMembersRepository.saveAll(members);
        }


        return newGroup;
    }
    
    public Groups getGroupById(Long id){
        Users currentUser = jwtService.getCurrentUser();
        Groups group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Group not found id: %d", id)));
        GroupMembers membership = groupMembersRepository.findByGroupIdAndUserId(group, currentUser)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        return group;
    }

    public List<GroupListDto> getAllGroups(){
        Users currentUser = jwtService.getCurrentUser();
        return groupRepository.findAllGroupListsByMember(currentUser);
    }

    public void addMemberToGroup(Long groupId, String email){
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException(String.format("Group not found id: %d", groupId)));
        if (!jwtService.getCurrentUser().equals(group.getCreatedBy())) {
            throw new RuntimeException("Only the creator of the group can add members");
        }
        Users newUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(String.format("User not found email: %s", email)));

        if (groupMembersRepository.findByGroupIdAndUserId(group, newUser).isPresent()) {
            throw new RuntimeException("User is already a member of this group");
        }
        GroupMembers groupMember= new GroupMembers();
        groupMember.setGroupId(group);
        groupMember.setUserId(newUser);
        groupMembersRepository.save(groupMember);
    }

}
