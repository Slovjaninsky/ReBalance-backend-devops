package com.rebalance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
@Table(name = "expense_user")
@Entity
public class ExpenseUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer multiplier = 1;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;
}
