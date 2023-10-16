package com.rebalance.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Table
@Entity
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private String currency;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    private Set<UserGroup> users = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    Set<Expense> expenses;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    Set<Category> categories;
}
