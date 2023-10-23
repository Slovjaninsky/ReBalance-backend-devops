package com.rebalance.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Table(name = "app_group")
@Entity
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String currency;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean personal = false;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    private Set<UserGroup> users = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    Set<Expense> expenses;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    Set<Category> categories;

    public Boolean isPersonalOf(User user) {
        return personal && creator.getId().equals(user.getId());
    }
}
