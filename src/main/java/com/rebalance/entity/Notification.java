package com.rebalance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "added_id")
    private User added;

    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "expense_description")
    private String expenseDescription;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "date")
    private LocalDateTime date;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "notification")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<NotificationUser> notificationUsers;

    public List<String> getMessage() {
        return switch (type) {
            case UserAddedToGroup ->
                    List.of(initiator.getNickname(), " added ", added.getNickname(), " to ", group.getName());
            case CurrentUserAddedToGroup -> List.of(initiator.getNickname(), " added you to ", group.getName());
            case GroupCreated -> List.of(initiator.getNickname(), " created group ", group.getName());
            case GroupExpenseAdded ->
                    List.of(initiator.getNickname(), " added ", expenseDescription, " to ", group.getName());
            case GroupExpenseEdited ->
                    List.of(initiator.getNickname(), " edited ", expenseDescription, " in ", group.getName());
            case GroupExpenseDeleted ->
                    List.of(initiator.getNickname(), " deleted ", expenseDescription, " from ", group.getName());
            case PersonalExpenseAdded -> List.of("Added personal expense ", expenseDescription);
            case PersonalExpenseEdited -> List.of("Edited personal expense ", expenseDescription);
            case PersonalExpenseDeleted -> List.of("Deleted personal expense ", expenseDescription);
        };
    }
}
