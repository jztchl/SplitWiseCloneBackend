package com.jztchl.splitwiseclonejava.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {
    private Long id;
    private String name;
    private String email;
    private String profilePictureUrl;
    private String createdAt;
    private Double liability;
    private Double exposure;

}
