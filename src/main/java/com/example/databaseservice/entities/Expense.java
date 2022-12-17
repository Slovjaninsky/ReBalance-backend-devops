package com.example.databaseservice.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Table
@Entity
@Data
@NoArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "expense_id")
    private Long id;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "description")
    private String description;

    @Column(name = "date_stamp")
    private LocalDate dateStamp;

    @Column(name = "category")
    private String category;

    @Column(name = "global_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long globalId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private ExpenseGroup group;

    public Expense(Integer amount, String description, String category) {
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    public Expense(Integer amount, String description, String category, ApplicationUser user, ExpenseGroup group) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.user = user;
        this.group = group;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", dateStamp=" + dateStamp +
                ", category='" + category + '\'' +
                ", globalId=" + globalId +
                ", user=" + user.getUsername() +
                ", group=" + group.getName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
