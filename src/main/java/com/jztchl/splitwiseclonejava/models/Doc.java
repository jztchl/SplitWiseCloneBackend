package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Doc extends BaseModel {
    @NotBlank(message = "Url is required")
    private String url;
}
