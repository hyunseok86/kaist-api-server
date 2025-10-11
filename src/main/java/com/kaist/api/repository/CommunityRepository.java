package com.kaist.api.repository;

import com.kaist.api.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    
    @Query("SELECT c FROM Community c ORDER BY c.crDt DESC")
    Page<Community> findAllOrderByCrDtDesc(Pageable pageable);
}
