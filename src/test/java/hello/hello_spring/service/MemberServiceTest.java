package hello.hello_spring.service;

import hello.hello_spring.domain.Member;
import hello.hello_spring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

// run 단축어 Mac: control + r, Window: shift + f10
class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    // Service에서와 같은 Repository를 사용하기 위해 Repository를 생성자로 넣어줌
    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }


    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void 회원가입() {
        // given (이런 상황이 주어졌을 때 => 데이터 기반)
        Member member = new Member();
        member.setName("spring");

        // when (이것을 실행했을 때)
        Long saveId = memberService.join(member);

        // then (이런 결과가 나와야 한다)
        Member findMember =  memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // when
        memberService.join(member1);
//        try {
//            memberService.join(member2);
//            fail(); // 예외가 발생하지 않으면 실패
//        } catch (IllegalStateException e) {
//            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
//        }
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

        // then
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}