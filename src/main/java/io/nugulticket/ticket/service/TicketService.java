package io.nugulticket.ticket.service;

import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.enums.TicketStatus;
import io.nugulticket.ticket.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    /**
     * 해당 Id를 가진 티켓을 반환하는 메서드
     * @param id 조회할 Id
     * @return 해당 Id를 가진 티켓 / 없을 경우 NotFoundException 발생
     */
    public Ticket findTicketById(Long id) {
        return ticketRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * 해당 유저가 구매한 Ticket중에 Reserved 상태인 Ticket만 반환하는 메서드
     * @param status 티켓 상태
     * @param userId 구매자 Id
     * @return 해당 구매자가 구매한 티켓 중 해당 상태인 티켓 리스트
     */
    public List<Ticket> findAllTicketByUserAndStatus(TicketStatus status, Long userId) {
        return ticketRepository.findAllByStatusAndBuyerId(status, userId);
    }
}
