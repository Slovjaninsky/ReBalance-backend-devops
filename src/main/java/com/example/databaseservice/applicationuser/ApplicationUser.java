package com.example.databaseservice.applicationuser;

import com.example.databaseservice.expense.Expense;
import com.example.databaseservice.group.ExpenseGroup;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Table
@Entity
@Data
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(name = "user_group",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")})
    private Set<ExpenseGroup> expenseGroups = new HashSet<>();

    public ApplicationUser() {
    }

    public ApplicationUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public ApplicationUser(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "user")
    private Set<Expense> expenses;

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
