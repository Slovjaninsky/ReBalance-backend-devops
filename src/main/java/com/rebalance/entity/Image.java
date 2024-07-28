package com.rebalance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
@Entity
public class Image {
    @Id
    @Column(name = "global_id")
    private Long globalId;

    @Column(name = "image_path")
    private String imagePath;
}
