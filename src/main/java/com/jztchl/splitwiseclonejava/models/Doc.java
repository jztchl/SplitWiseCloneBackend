package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Doc extends BaseModel {
    private String url;
}
