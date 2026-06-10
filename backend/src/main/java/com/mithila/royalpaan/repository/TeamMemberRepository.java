package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
    List<TeamMember> findAllByOrderByDisplayOrderAsc();
}
