package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.response.file.ImageDto;
import com.hackathonteam1.refreshrator.entity.Image;
import com.hackathonteam1.refreshrator.entity.Recipe;
import com.hackathonteam1.refreshrator.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService{
    //파일(이미지) 등록
    public ImageDto registerImage(MultipartFile file);

    //파일(이미지) 삭제
    public void deleteImage(UUID imageId, User user);

    //이미지Id로 이미지 찾기
    public Image findImageById(UUID imageId);

    //레시피로 이미지 찾기
    public Image findImageByRecipe(Recipe recipe);

    //유저의 레시피 내 S3 이미지들을 전부 삭제
    public void deleteAllImagesOfUser(User user);

}