package io.nugulticket.ticket.entity;

import io.nugulticket.ticket.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(nullable = false)
    private Long buyerId;

    // @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // private Seat seat;

    private String qrCode;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    // 양방향 ? 단방향 ? 의논 후
    //@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    //private List<TicketTransfer> transfers;

    /**
     * 티켓 상태를 변화 시키는 메서드
     * @param newStatus 새로운 상태 ( RESERVED, CANCELLED, WAITTRANSFER, TRANSFERRED, AUCTION ... )
     */
    public void changeStatus(TicketStatus newStatus) {
        this.status = newStatus;
    }
}