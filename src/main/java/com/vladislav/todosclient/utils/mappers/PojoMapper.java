package com.vladislav.todosclient.utils.mappers;

public interface PojoMapper<DOC, DTO> {
    DOC toDocument(DTO dto);

    DTO toDto(DOC document);
}
