package com.ejh.welcome.domain.member.application;

import com.ejh.welcome.domain.member.domain.Member;
import com.ejh.welcome.domain.member.domain.MemberRepository;
import com.ejh.welcome.domain.member.domain.RoleType;
import com.ejh.welcome.domain.member.dto.MemberResponse;
import com.ejh.welcome.domain.member.exception.MemberNotFoundException;
import com.ejh.welcome.global.error.exception.ErrorCode;
import javassist.bytecode.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    /**
     * 회원가입
     */
    public String signUp(Member requestMember) throws DuplicateMemberException {
        isDuplicate(requestMember);

        Member member = Member.builder()
                .email(requestMember.getEmail())
                .name(requestMember.getName())
                .password(encoder.encode(requestMember.getPassword()))
                .nickname(requestMember.getNickname())
                .birth(requestMember.getBirth())
                .roleType(RoleType.USER)
                .build();

        Member savedMember = memberRepository.save(member);

        return savedMember.getEmail();
    }

    /**
     * 회원정보 조회
     */
    @Transactional(readOnly = true)
    public MemberResponse findByEmail(final String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponse.from(member);
    }

    private void isDuplicate(Member member) throws DuplicateMemberException {
        if (memberRepository.existsByEmail(member.getEmail()) ||
                memberRepository.existsByNickname(member.getNickname())) {
            throw new DuplicateMemberException("이미 존재하는 닉네임 혹은 이메일입니다.");
        }
    }

}
