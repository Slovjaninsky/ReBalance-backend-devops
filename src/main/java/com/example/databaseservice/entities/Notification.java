package com.example.databaseservice.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table
@Entity
@Data
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "group_id")
    private Long groupId;

    public Notification(Long userId, Long expenseOrGroupId, Boolean isExpense) {
        this.userId = userId;
        if (isExpense) {
            this.expenseId = expenseOrGroupId;
        } else {
            this.groupId = expenseOrGroupId;
        }
    }
}
