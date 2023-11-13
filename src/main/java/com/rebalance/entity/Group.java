package com.rebalance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<UserGroup> users = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Expense> expenses;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<GroupCategory> categories = new HashSet<>();

    public Boolean isPersonalOf(User user) {
        return personal && creator.getId().equals(user.getId());
    }
}
