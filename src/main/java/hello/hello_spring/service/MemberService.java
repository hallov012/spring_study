package hello.hello_spring.service; // respository와 domain을 사용해 실제 비즈니스 로직을 구현하는 곳

import hello.hello_spring.domain.Member;
import hello.hello_spring.repository.MemberRepository;
import hello.hello_spring.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

//@Service // 스프링이 MemberService를 생성해서 스프링 컨테이너에 넣어둠
@Transactional
public class MemberService {

    // 테스트 생성 단축어 Mac: command + shift + t, Window: ctrl + shift + t

    private final MemberRepository memberRepository;

    /**
     * Test에서도 동일한 MemoryMemberRepository를 사용하기 위해 생성자를 통해 외부에서 넣어줌
     */
//    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원가입
     */
    public Long join(Member member) {
        // 같은 이름이 있는 중복 회원X
//        Optional<Member> result = memberRepository.findByName(member.getName());
//        result.ifPresent(m -> {
//            throw new IllegalStateException("이미 존재하는 회원입니다.");
//        });
        // Optional을 바로 반환하는 것이 그리 좋은 방법은 아님

        validateDuplicateMember(member);

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 조회
     */
    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
