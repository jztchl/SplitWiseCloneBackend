package com.jztchl.splitwiseclonejava.repos;

import com.jztchl.splitwiseclonejava.models.GroupMembers;
import com.jztchl.splitwiseclonejava.models.Groups;
import com.jztchl.splitwiseclonejava.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GroupMembersRepository extends JpaRepository<GroupMembers,Long>{
    List<GroupMembers> findAllByUserId(Users userId);
    Optional<GroupMembers> findByGroupIdAndUserId(Groups group, Users currentUser);
}
