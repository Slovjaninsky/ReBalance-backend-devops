package com.example.databaseservice.expense;

import com.example.databaseservice.applicationuser.ApplicationUser;
import com.example.databaseservice.group.ExpenseGroup;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Table
@Entity
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "expense_id")
    private Long id;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private ExpenseGroup group;

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                ", user=" + user.getUsername() +
                ", group=" + group.getName() +
                '}';
    }
}
