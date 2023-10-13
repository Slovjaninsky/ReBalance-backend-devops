package com.rebalance.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@Table
@Entity
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "currency")
    private String currency;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    private Set<UserGroup> users = new HashSet<>();

    @OneToMany(mappedBy = "group")
    @JsonIgnore
    Set<Expense> expenses;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    Set<Category> categories;
}
