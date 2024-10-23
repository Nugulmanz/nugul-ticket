package io.nugulticket.ticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ticket_transfer")
public class TicketTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "transfer_price", nullable = false)
    int price;

    @Column(name = "transfer_at", nullable = false)
    LocalDateTime transferAt;

    public TicketTransfer(Ticket ticket, Long userId) {
        this.ticket = ticket;
        this.userId = userId;
        this.price = ticket.getSeat().getPrice();
        this.transferAt = LocalDateTime.now();
    }
}
