package com.rebalance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Table(name = "notifications")
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private NotificationType type;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "expense_description")
    private String expenseDescription;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "notification")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<NotificationUser> notificationUsers;

    public List<String> getMessage() {
        return switch (type) {
            case UserAddedToGroup -> List.of(initiator.getNickname(), " added you to ", group.getName());
            case GroupExpenseAdded ->
                    List.of(initiator.getNickname(), " added ", expenseDescription, " to ", group.getName());
            case GroupExpenseEdited ->
                    List.of(initiator.getNickname(), " edited ", expenseDescription, " to ", group.getName());
            case GroupExpenseDeleted ->
                    List.of(initiator.getNickname(), " deleted ", expenseDescription, " to ", group.getName());
        };
    }
}
