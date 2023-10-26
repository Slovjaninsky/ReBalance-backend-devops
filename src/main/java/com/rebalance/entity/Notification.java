package com.rebalance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "notifications")
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_from_id")
    private Long userFromId;

    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "amount")
    private Double amount;

    public Notification(Long userId, Long userFromId, Long expenseOrGroupId, Double amount, Boolean isExpense) {
        this.userId = userId;
        this.userFromId = userFromId;
        this.amount = amount;
        if (isExpense) {
            this.expenseId = expenseOrGroupId;
        } else {
            this.groupId = expenseOrGroupId;
        }
    }
}
