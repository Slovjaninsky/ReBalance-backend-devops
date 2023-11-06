package com.rebalance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@Table(name = "expenses")
@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String category;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "added_by_id", nullable = false)
    private User addedBy;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "expense")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ExpenseUsers> expenseUsers;
}
