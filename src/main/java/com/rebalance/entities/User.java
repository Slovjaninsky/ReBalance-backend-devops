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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(name = "user_group",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")})
    @JsonIgnore
    private Set<ExpenseGroup> expenseGroups = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Expense> expenses;

    public ApplicationUser() {
    }

    public ApplicationUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public ApplicationUser(Long id) {
        this.id = id;
    }

    public void addGroup(ExpenseGroup expenseGroup) {
        this.expenseGroups.add(expenseGroup);
        expenseGroup.getUsers().add(this);
    }

    public void removeGroup(long groupId) {
        ExpenseGroup group = this.expenseGroups.stream().filter(t -> t.getId() == groupId).findFirst().orElse(null);
        if (group != null) {
            this.expenseGroups.remove(group);
            group.getUsers().remove(this);
        }
    }

    @Override
    public String toString() {
        return "ApplicationUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationUser that = (ApplicationUser) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
