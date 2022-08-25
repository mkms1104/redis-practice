package com.example.passwordChanger.api;

import com.example.passwordChanger.authStrategy.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

/**
 * 미인증 사용자를 위한 비밀번호 변경 API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class PasswordChangeApi {
    private final AuthUser authUser;

    // 1. 사용자 인증 화면
    // 미인증 사용자임으로 사용자를 인증할 수 있는 절차가 필요하다.
    // ex) 핸드폰 인증, 이메일 인증

    // 2. 비밀번호 변경 화면
    // 인증에 성공한 사용자는 비밀번호 변경 화면으로 이동한다. (사용자를 구분할 수 있는 값을 같이 전달)
    // 구분 값은 만료 시간을 지정하여 사용자의 브라우저 쿠키에 저장한다.
    // 서버는 최종 비밀번호 변경 api 호출 시 쿠키 값을 확인한다.
    // 없으면 제한 시간 만료
    // 있으면 올바른 사용자인지 확인


//    @GetMapping("auth/change-password")
//    public ResponseEntity authUserWithEmail(@RequestParam("email") String email) {
//        if (!authUser.isAuth()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//
//        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body("인증 구분 값");
//    }

    @GetMapping("auth/change-password")
    public ResponseEntity authUserWithPhoneNumber(@RequestParam("phone_number") String phoneNumber) {
        log.debug("call authUserWithPhoneNumber");
        if (!authUser.isAuth()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ResponseCookie cookie = ResponseCookie.from("authKey", "mskim")
                .maxAge(Duration.ofSeconds(10))
                .build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .location(URI.create("/change?authKey=1"))
                .contentType(MediaType.TEXT_PLAIN)
                .body("아몰랑");
    }

    @PostMapping("auth/change-password")
    public ResponseEntity changePassword(@CookieValue("authKey") String authKey) {
        log.debug("call changePassword");
        log.debug(authKey);
        if (Objects.isNull(authKey)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 키 만료");
        }

        return ResponseEntity.ok().body("변경 성공");
    }
}
