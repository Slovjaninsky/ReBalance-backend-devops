package com.example.databaseservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @Column(name = "global_id")
    private Long id;

    @Column(name = "image_path")
    private String imagePath;
}
