package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.group.CreateGroupDto;
import com.jztchl.splitwiseclonejava.dtos.group.GroupDetailsDto;
import com.jztchl.splitwiseclonejava.dtos.group.GroupListDto;
import com.jztchl.splitwiseclonejava.models.GroupMembers;
import com.jztchl.splitwiseclonejava.models.Groups;
import com.jztchl.splitwiseclonejava.models.Users;
import com.jztchl.splitwiseclonejava.repos.GroupMembersRepository;
import com.jztchl.splitwiseclonejava.repos.GroupRepository;
import com.jztchl.splitwiseclonejava.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("GroupService Test")
class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupMembersRepository groupMembersRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Users currentuser;
    private Users testUser2;
    private Users testUser3;


    @BeforeEach
    void setUp() {
        this.currentuser = new Users();
        this.currentuser.setId(1);
        this.currentuser.setName("User1");
        this.currentuser.setEmail("currentuser@example.com");

        this.testUser2 = new Users();
        this.testUser2.setId(2);
        this.testUser2.setName("User2");
        this.testUser2.setEmail("testuser2@example.com");

        this.testUser3 = new Users();
        this.testUser3.setId(3);
        this.testUser3.setName("User3");
        this.testUser3.setEmail("testuser3@example.com");


    }

    @InjectMocks
    private GroupService groupService;

    @Nested
    @DisplayName("Test Create Group")
    class CreateGroupTest {

        @Test
        @DisplayName("Should create a group successfully with valid members")
        void createGroup_WithValidMembers_ShouldSucceed() {
            //Given
            CreateGroupDto groupDto = new CreateGroupDto();
            groupDto.setGroupDescription("Test Group Description");
            groupDto.setGroupName("Test Group");
            List<String> members = List.of(testUser2.getEmail(), testUser3.getEmail());
            groupDto.setMembers(members);
            Groups group = new Groups();
            group.setId(1L);
            group.setGroupName("Test Group");
            group.setDescription("Test Group Description");
            group.setCreatedBy(currentuser);

            GroupMembers groupMember1 = new GroupMembers();
            groupMember1.setGroupId(group);
            groupMember1.setUserId(currentuser);

            GroupMembers groupMember2 = new GroupMembers();
            groupMember2.setGroupId(group);
            groupMember2.setUserId(testUser2);

            GroupMembers groupMember3 = new GroupMembers();
            groupMember3.setGroupId(group);
            groupMember3.setUserId(testUser3);


            List<GroupMembers> memberList = List.of(groupMember1, groupMember2, groupMember3);
            group.setMembers(memberList);


            when(jwtService.getCurrentUser()).thenReturn(currentuser);
            when(userRepository.findByEmail(testUser2.getEmail())).thenReturn(Optional.of(testUser2));
            when(userRepository.findByEmail(testUser3.getEmail())).thenReturn(Optional.of(testUser3));
            when(groupRepository.save(any(Groups.class))).thenReturn(group);

            //When
            Groups newGroup = groupService.createGroup(groupDto);


            //Then
            assertEquals(group.getId(), newGroup.getId());
            assertEquals(group.getGroupName(), newGroup.getGroupName());
            assertEquals(group.getDescription(), newGroup.getDescription());
            assertEquals(group.getCreatedBy(), newGroup.getCreatedBy());
            verify(groupRepository, times(1)).save(any(Groups.class));
            verify(groupMembersRepository, times(1)).saveAll(any());


        }

        @Test
        @DisplayName("Should throw RuntimeException when a member email does not exist")
        void createGroup_WithInvalidMember_ShouldThrowException() {
            // Given
            CreateGroupDto groupDto = new CreateGroupDto();
            groupDto.setGroupDescription("Test Group Description");
            groupDto.setGroupName("Test Group With Invalid Member");
            String invalidEmail = "nonexistent@example.com";
            List<String> members = List.of(testUser2.getEmail(), invalidEmail);
            groupDto.setMembers(members);

            when(jwtService.getCurrentUser()).thenReturn(currentuser);
            when(userRepository.findByEmail(testUser2.getEmail())).thenReturn(Optional.of(testUser2));
            when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                groupService.createGroup(groupDto);
            });

            assertEquals(String.format("User not found email: %s", invalidEmail), exception.getMessage());


            // The service should fail fast before attempting to save the group
            verify(groupRepository, times(1)).save(any(Groups.class));
            verify(groupMembersRepository, never()).saveAll(any());
            verify(eventPublisher, never()).publishEvent(any());
        }


        @Test
        @DisplayName("Should return a list of groups for a user with groups")
        void getAllGroups_ForUserWithGroups_ShouldReturnGroupList() {
            // Given
            GroupListDto dto1 = mock(GroupListDto.class);
            GroupListDto dto2 = mock(GroupListDto.class);
            List<GroupListDto> mockGroupList = List.of(dto1, dto2);

            when(jwtService.getCurrentUser()).thenReturn(currentuser);
            when(groupRepository.findAllGroupListsByMember(currentuser)).thenReturn(mockGroupList);

            // When
            List<GroupListDto> result = groupService.getAllGroups();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertSame(mockGroupList, result);
            verify(groupRepository).findAllGroupListsByMember(currentuser);
        }

        @Test
        @DisplayName("Should return an empty list for a user with no groups")
        void getAllGroups_ForUserWithNoGroups_ShouldReturnEmptyList() {
            // Given
            when(jwtService.getCurrentUser()).thenReturn(testUser2);
            when(groupRepository.findAllGroupListsByMember(testUser2)).thenReturn(List.of());

            // When
            List<GroupListDto> result = groupService.getAllGroups();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(groupRepository).findAllGroupListsByMember(testUser2);
        }

        @Test
        @DisplayName("Should return a valid group if present and have access to group")
        void getGroupById_WithValidGroupAndAccess_ShouldReturnGroup() {
            // Given
            Long groupId = 1L;
            Groups group = new Groups();
            group.setId(groupId);
            group.setGroupName("Test Group");
            group.setDescription("Test Group Description");
            group.setCreatedBy(currentuser);

            GroupMembers groupMember = new GroupMembers();
            groupMember.setGroupId(group);
            groupMember.setUserId(currentuser);

            List<GroupMembers> memberList = List.of(groupMember);
            group.setMembers(memberList);

            when(jwtService.getCurrentUser()).thenReturn(currentuser);
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
            when(groupMembersRepository.existsByGroupIdAndUserId(group, currentuser)).thenReturn(true);

            // When
            GroupDetailsDto result = groupService.getGroupById(groupId);

            // Then
            assertNotNull(result);
            assertEquals(groupId, result.getId());

        }


        @Test
        @DisplayName("Should throw RuntimeException if group is not found")
        void getGroupById_WithInvalidGroupId_ShouldThrowException() {
            // Given
            Long groupId = 1L;

            when(jwtService.getCurrentUser()).thenReturn(currentuser);
            when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                groupService.getGroupById(groupId);
            });
            assertEquals(String.format("Group not found id: %d", groupId), exception.getMessage());
        }

        @Test
        @DisplayName("Should throw RuntimeException if don't have access to group")
        void getGroupById_WithInvalidAccess_ShouldThrowException() {
            // Given
            Long groupId = 1L;
            Groups group = new Groups();
            group.setId(groupId);
            group.setGroupName("Test Group");
            group.setDescription("Test Group Description");
            group.setCreatedBy(currentuser);

            GroupMembers groupMember = new GroupMembers();
            groupMember.setGroupId(group);
            groupMember.setUserId(testUser2);
            List<GroupMembers> memberList = List.of(groupMember);
            group.setMembers(memberList);
            when(jwtService.getCurrentUser()).thenReturn(currentuser);
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
            when(groupMembersRepository.existsByGroupIdAndUserId(group, currentuser)).thenReturn(false);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                groupService.getGroupById(groupId);
            });
            assertEquals("You are not a member of this group", exception.getMessage());
        }


    }
}