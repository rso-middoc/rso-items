package src.main.java.si.fri.rso.middoc.models.converters;

import src.main.java.si.fri.rso.middoc.models.entities.ItemEntity;

public class ItemsConverter {

    public static src.main.java.si.fri.rso.middoc.lib.Item toDto(ItemEntity entity) {

        src.main.java.si.fri.rso.middoc.lib.Item dto = new src.main.java.si.fri.rso.middoc.lib.Item();
        dto.setItemId(entity.getId());
        dto.setCreated(entity.getCreated());
        dto.setDescription(entity.getDescription());
        dto.setTitle(entity.getTitle());
        dto.setFormat(entity.getFormat());
        dto.setUri(entity.getUri());
        dto.setCollectionId(entity.getCollectionId());

        return dto;

    }

    public static ItemEntity toEntity(src.main.java.si.fri.rso.middoc.lib.Item dto) {

        ItemEntity entity = new ItemEntity();
        entity.setCreated(dto.getCreated());
        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());
        entity.setFormat(dto.getFormat());
        entity.setUri(dto.getUri());
        entity.setCollectionId(dto.getCollectionId());

        return entity;

    }

}
