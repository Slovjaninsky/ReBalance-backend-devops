package com.example.databaseservice.group;

import com.example.databaseservice.applicationuser.ApplicationUser;
import com.example.databaseservice.expense.Expense;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

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

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "expenseGroups")
    @JsonIgnore
    private Set<ApplicationUser> users = new HashSet<>();

    public ExpenseGroup() {
    }

    public ExpenseGroup(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "group")
    Set<Expense> expenses;

    @Override
    public String toString() {
        return "ExpenseGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
