package com.rebalance.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    private Double amount;

    @Column(name = "description")
    private String description;

    @Column(name = "date_stamp")
    private LocalDate dateStamp;

    @Column(name = "category")
    private String category;

    @Column(name = "global_id")
    private Long globalId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private ExpenseGroup group;

    public Expense(Double amount, String description, String category) {
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "expense")
    private Set<ExpenseUsers> expenseUsers;
}
