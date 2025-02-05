package com.project.backend.domain.member.controller;

import com.project.backend.domain.member.dto.LoginDto;
import com.project.backend.domain.member.dto.MemberDto;
import com.project.backend.domain.member.dto.MineDto;
import com.project.backend.domain.member.dto.PasswordDto;
import com.project.backend.domain.member.entity.Member;
import com.project.backend.domain.member.exception.MemberException;
import com.project.backend.domain.member.service.MemberService;
import com.project.backend.global.jwt.JwtUtil;
import com.project.backend.global.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.project.backend.domain.member.exception.MemberErrorCode.*;

/**
 * 회원 컨트롤러
 *
 * @author 손진영
 * @since 25. 1. 27.
 */
@Tag(name = "MemberController", description = "회원 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {
    private final MemberService memberService;
    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입 요청
     *
     * @param memberDto
     * @return GenericResponse<MemberDto>
     * @Valid
     * @author 손진영
     * @since 2025.01.27
     */
    @PostMapping
    @Operation(summary = "회원가입")
    public GenericResponse<MemberDto> join(@RequestBody @Valid MemberDto memberDto) {
        Member member = memberService.join(memberDto);

        return GenericResponse.of(
                new MemberDto(member),
                "회원가입 성공"
        );
    }

    /**
     * 로그인 요청
     *
     * @param loginDto
     * @return GenericResponse<MemberDto>
     * @author 손진영
     * @since 2025.01.27
     */
    @PostMapping("/login")
    @Operation(summary = "로그인")
    public GenericResponse<MemberDto> login(@RequestBody @Valid LoginDto loginDto) {
        Member member = memberService.getMember(loginDto.getUsername())
                .orElseThrow(() -> new MemberException(NON_EXISTING_ID));

        if (!member.getPassword().equals(loginDto.getPassword()))
            throw new MemberException(INCORRECT_PASSWORD);

        System.out.println("jwtUtil.generateToken = " + jwtUtil.generateToken(member.getUsername()));

        return GenericResponse.of(
                new MemberDto(member),
                "로그인 성공"
        );
    }

    /**
     * 회원 정보 조회
     *
     * @return GenericResponse<MemberDto>
     * @author 손진영
     * @since 2025.01.27
     */
    @GetMapping("/mine")
    @Operation(summary = "회원 정보 조회")
    public GenericResponse<MemberDto> mine() {

        Member member = getUser();

        return GenericResponse.of(
                new MemberDto(member),
                "회원 정보 조회 성공"
        );
    }

    /**
     * 회원 정보 수정
     *
     * @param mineDto
     * @return GenericResponse<MemberDto>
     * @Valid
     * @author 손진영
     * @since 2025.01.28
     */
    @PutMapping("/mine")
    @Transactional
    @Operation(summary = "회원 정보 수정")
    public GenericResponse<MemberDto> mine(@RequestBody @Valid MineDto mineDto) {

        Member member = getUser();

        memberService.modify(member,
                mineDto.getPassword(),
                mineDto.getEmail(),
                mineDto.getGender(),
                mineDto.getNickname(),
                mineDto.getBirth());

        return GenericResponse.of(
                new MemberDto(member),
                "회원 정보 수정 성공"
        );
    }

    /**
     * 회원 탈퇴
     *
     * @param passwordDto
     * @return GenericResponse
     * @Valid
     * @author 손진영
     * @since 2025.01.31
     */
    @DeleteMapping("/mine")
    @Operation(summary = "회원 탈퇴")
    public GenericResponse mine(@RequestBody @Valid PasswordDto passwordDto) {
        Member member = getUser();

        if (!passwordDto.getPassword().equals(member.getPassword()))
            throw new MemberException(INCORRECT_AUTHORIZED);

        memberService.delete(member);

        return GenericResponse.of("탈퇴 성공");
    }

    /**
     * Request Authorization Check
     *
     * @return Member
     * @author 손진영
     * @since 2025.01.31
     */
    private Member getUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        UserDetails user = (UserDetails) authentication.getPrincipal();

        return memberService.getMember(user.getUsername())
                .orElseThrow(() -> new MemberException(INCORRECT_AUTHORIZED));
    }
}
