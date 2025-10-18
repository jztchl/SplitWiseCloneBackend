package com.jztchl.splitwiseclonejava.repos;

import com.jztchl.splitwiseclonejava.models.GroupMembers;
import com.jztchl.splitwiseclonejava.models.Groups;
import com.jztchl.splitwiseclonejava.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMembersRepository extends JpaRepository<GroupMembers, Long> {
    @Query("SELECT gm FROM GroupMembers gm WHERE gm.groupId = :groupId AND gm.userId.id IN :userIds")
    List<GroupMembers> findAllByGroupfindUserIds(
            @Param("groupId") Groups groupId,
            @Param("userIds") List<Long> userIds);

    Optional<GroupMembers> findByGroupIdAndUserId(Groups group, Users currentUser);

}
