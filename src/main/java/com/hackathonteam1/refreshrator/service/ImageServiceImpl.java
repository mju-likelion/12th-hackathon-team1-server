package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.response.file.ImageDto;
import com.hackathonteam1.refreshrator.entity.Image;
import com.hackathonteam1.refreshrator.entity.Recipe;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.BadRequestException;
import com.hackathonteam1.refreshrator.exception.FileStorageException;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.ImageRepository;
import com.hackathonteam1.refreshrator.repository.RecipeRepository;
import com.hackathonteam1.refreshrator.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;
    private final S3Uploader s3Uploader;
    private final RecipeRepository recipeRepository;

    private static final List<String> IMAGE_EXTENSION = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".tiff", ".svg", ".heic");


    @Override
    public ImageDto registerImage(MultipartFile file) {

        validateImageFile(file); //확장자를 통해 이미지 파일인지 확인

        String url = uplaodFileToS3(file);

        Image image = Image.builder()
                .url(url)
                .build();

        imageRepository.save(image);
        ImageDto imageDto = ImageDto.mapping(image);
        return imageDto;
    }

    @Override
    public void deleteImage(UUID imageId, User user) {
        Image image = findImageById(imageId);
        s3Uploader.removeS3FileByUrl(image.getUrl());
        imageRepository.delete(image);
    }

    @Override
    public Image findImageById(UUID imageId){
        return imageRepository.findById(imageId).orElseThrow(()->new NotFoundException(ErrorCode.IMAGE_NOT_FOUND));
    }

    @Override
    public Image findImageByRecipe(Recipe recipe){
        return imageRepository.findByRecipe(recipe).orElseThrow(()->new NotFoundException(ErrorCode.IMAGE_NOT_FOUND));
    }

    @Override
    @CacheEvict(value = "recipeListCache", allEntries = true, cacheManager = "redisCacheManager")
    public void deleteAllImagesOfUser(User user) {
        List<Recipe> recipes = findAllRecipesByUser(user);

        recipes.forEach(recipe-> {
            if(recipe.isContainingImage()){
                Image image = findImageByRecipe(recipe);
                s3Uploader.removeS3FileByUrl(image.getUrl());
            }
        });
    }

    private void validateImageFile(MultipartFile file){
        String lowerFileName = file.getOriginalFilename().toLowerCase();
        if(!IMAGE_EXTENSION.stream().anyMatch(i-> lowerFileName.endsWith(i))){
            throw new FileStorageException(ErrorCode.FILE_TYPE_ERROR);
        };
    }

    private String uplaodFileToS3(MultipartFile file){
        try {
            return s3Uploader.upload(file);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.FILE_STORAGE_ERROR, e.getMessage());
        }
    }

    private List<Recipe> findAllRecipesByUser(User user){
        return recipeRepository.findAllByUser(user).orElseThrow(()-> new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
    }
}
