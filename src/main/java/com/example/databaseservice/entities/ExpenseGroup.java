package com.example.databaseservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Table(name = "expense_group")
@Entity
@Data
public class ExpenseGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "currency")
    private String currency;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "expenseGroups")
    private Set<ApplicationUser> users = new HashSet<>();

    @OneToMany(mappedBy = "group")
    @JsonIgnore
    Set<Expense> expenses;

    public ExpenseGroup() {
    }

    public ExpenseGroup(String name, String currency) {
        this.name = name;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "ExpenseGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseGroup that = (ExpenseGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
