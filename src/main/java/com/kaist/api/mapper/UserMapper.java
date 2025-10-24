package com.kaist.api.mapper;

import org.springframework.data.repository.query.Param;


import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public List<HashMap<String, Object>> findByUserId(@Param("userId") String userId);
}
