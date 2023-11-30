package com.rebalance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "category_id")
    private GroupCategory category;

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
