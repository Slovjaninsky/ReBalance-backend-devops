package com.rebalance.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "images")
@Entity
public class Image {
    @Id
    @Column(name = "global_id")
    private Long id;

    @Column(name = "image_path")
    private String imagePath;
}
