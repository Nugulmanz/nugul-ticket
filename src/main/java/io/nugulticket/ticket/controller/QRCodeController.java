package io.nugulticket.ticket.controller;


import io.nugulticket.ticket.config.QrCodeUtil;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qr")
public class QRCodeController {

    private final TicketService ticketService;

    @GetMapping(value = "/{ticketId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long ticketId) {
        try {
            String qrCodeText = ticketService.getQRCodeByTicketId(ticketId);
            byte[] qrCodeImage = QrCodeUtil.getQRCodeImage(qrCodeText, 250, 250);

            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeImage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}