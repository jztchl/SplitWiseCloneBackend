package com.jztchl.splitwiseclonejava.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProfileDto {
    private Long id;
    private String name;
    private String email;
    private String profilePictureUrl;
    private String createdAt;
    private BigDecimal liability;


}
