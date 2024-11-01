package io.nugulticket.email.service;

import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${email.setForm}")
    private String setForm;
    private static final String EMAIL_TITLE = "회원 가입을 위한 인증 이메일";
    private static final String EMAIL_CONTENT_TEMPLATE = "아래의 인증번호를 입력하여 회원가입을 완료해주세요." +
            "<br><br>" +
            "인증번호 %s";

    public String joinEmail(String email) {
        String code = Integer.toString(makeRandomNumber());
        String content = String.format(EMAIL_CONTENT_TEMPLATE, code);
        mailSend(setForm, email, EMAIL_TITLE, content);
        return code;
    }

    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
            System.out.println("이메일 발송 성공: " + toMail);
        } catch (MessagingException e) {
            System.err.println("이메일 발송 실패: " + toMail);
            e.printStackTrace();
            throw new ApiException(ErrorStatus._EMAIL_SEND_ERROR);
        }
    }

    public int makeRandomNumber() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }
        return Integer.parseInt(randomNumber.toString());
    }
}
