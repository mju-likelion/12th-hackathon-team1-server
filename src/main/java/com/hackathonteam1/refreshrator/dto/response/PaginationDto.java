package com.hackathonteam1.refreshrator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDto {
    private int currentPage;
    private int totalPage;

    public static <T> PaginationDto paginationDto(Page<T> page){
        return new PaginationDto(page.getNumber(), page.getTotalPages());
    }
}
