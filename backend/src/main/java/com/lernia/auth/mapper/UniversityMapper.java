package com.lernia.auth.mapper;

import com.lernia.auth.dto.UniversityDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.entity.UniversityEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UniversityMapper {
    UniversityDTO toDTO(UniversityEntity entity);
    UniversityDTOLight toDTOLight(UniversityEntity entity);
}
