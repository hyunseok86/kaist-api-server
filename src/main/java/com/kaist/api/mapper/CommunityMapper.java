package com.kaist.api.mapper;

import org.springframework.data.repository.query.Param;

import com.kaist.api.entity.Community;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommunityMapper {
    public Community findByCommunityId(@Param("communityId") Long communityId);
}
