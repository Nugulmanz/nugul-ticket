package io.nugulticket.ticket.entity;

import io.nugulticket.event.entity.Event;
import io.nugulticket.seat.entity.Seat;
import io.nugulticket.ticket.enums.TicketStatus;
import io.nugulticket.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Seat seat;

    @Column(nullable = false)
    private String qrCode;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(nullable = false)
    private boolean isDeleted=false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "event_id")
    private Event event;

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

    public void createTicket(Event event, Seat seat, User user, String qrCode){
        this.event = event;
        this.seat = seat;
        this.qrCode = qrCode;
        this.purchaseDate = LocalDateTime.now();
        this.user = user;
        this.status=TicketStatus.RESERVED;
    }

    public void requestCancel(){
        this.status=TicketStatus.WAIT_CANCEL;
    }
    public void cancel(){
        this.status=TicketStatus.CANCELLED;
    }
}
