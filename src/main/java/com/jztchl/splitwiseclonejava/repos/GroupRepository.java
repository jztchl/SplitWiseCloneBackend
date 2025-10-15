package com.jztchl.splitwiseclonejava.repos;

import com.jztchl.splitwiseclonejava.dtos.GroupListDto;
import com.jztchl.splitwiseclonejava.models.Groups;
import com.jztchl.splitwiseclonejava.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Groups,Long> {
    @Query("SELECT g.id AS id, g.groupName AS groupName, g.createdBy.name AS groupOwner " +
            "FROM Groups g JOIN GroupMembers gm ON gm.groupId = g " +
            "WHERE gm.userId = :user")
    List<GroupListDto> findAllGroupListsByMember(@Param("user") Users user);

    // Returns groups where the given user is the owner (createdBy)
    @Query("SELECT g.id AS id, g.groupName AS groupName, g.createdBy.name AS groupOwner " +
            "FROM Groups g WHERE g.createdBy = :owner")
    List<GroupListDto> findAllByOwner(@Param("owner") Users owner);
}
