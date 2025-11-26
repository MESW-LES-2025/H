package com.lernia.auth.mapper;


import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.entity.CourseEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDTO toDTO(CourseEntity entity);
}
