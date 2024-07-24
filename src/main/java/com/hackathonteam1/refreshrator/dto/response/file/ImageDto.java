package com.hackathonteam1.refreshrator.dto.response.file;

import com.hackathonteam1.refreshrator.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ImageDto {

    private UUID id;
    private String url;

    public static ImageDto mapping(Image image){
        if(image==null){
            return null;
        }
        return new ImageDto(image.getId(), image.getUrl());
    }
}
