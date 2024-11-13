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

    private static final String LOCKED_EMAIL_TITLE = "계정 잠금 해제 인증 이메일";
    private static final String LOCKED_EMAIL_CONTENT_TEMPLATE = "아래의 인증번호를 입력하여 계정 잠금을 해제해주세요." +
            "<br><br>" +
            "인증번호 %s";

    /**
     * 인증 메일에 필요한 내용을 추가해주는 메서드
     * @param email 인증 메일을 수신할 이메일 주소
     * @return 인증에 사용될 6자리 인증번호
     */
    public String joinEmail(String email) {
        String code = Integer.toString(makeRandomNumber());
        String content = String.format(EMAIL_CONTENT_TEMPLATE, code);
        mailSend(setForm, email, EMAIL_TITLE, content);
        return code;
    }

    public String sendUnlockEmail(String email) {
        String code = Integer.toString(makeRandomNumber());
        String content = String.format(LOCKED_EMAIL_CONTENT_TEMPLATE, code);
        mailSend(setForm, email, LOCKED_EMAIL_TITLE, content);
        return code;
    }

    /**
     * 인증 메일을 전송하는 메서드
     * @param setFrom 메시지를 발신할 Naver 아이디
     * @param toMail 인증 메일을 수신할 이메일 주소
     * @param title 인증 이메일 제목
     * @param content 인증 정보가 담긴 이메일 내용
     */
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

    /**
     * 무작위 숫자를 만들어주는 메서드
     * @return 완성된 6자리의 무작위 숫자
     */
    public int makeRandomNumber() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }
        return Integer.parseInt(randomNumber.toString());
    }
}
