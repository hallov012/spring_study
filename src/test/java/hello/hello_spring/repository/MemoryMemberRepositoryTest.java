package hello.hello_spring.repository;

import hello.hello_spring.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    /**
     * Test의 순서는 보장이 되지 않기 떄문에, 각각의 Test는 독립적으로 실행되어야 한다.
     * 따라서, Test가 끝날 때마다 repository를 clear해주어야 한다.
     * 서로 순서와 관계 없이, 의존관계 없이 설계 되어야 함
     */
    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member();
        member.setName("spring");

        repository.save(member);

        Member result = repository.findById(member.getId()).get(); // 반환 타입이 Optional이기 때문에 get()으로 꺼내기
        // System.out.println("result = " + (result == member));
        // Assertions.assertEquals(member, result); // member와 result가 같은지 확인
        assertThat(member).isEqualTo(result);
    }

    @Test
    public void findByName() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring1");
        repository.save(member2);

        Member result = repository.findByName("spring1").get();
        assertThat(member1).isEqualTo(result);

//        Fail 예제
//        Member result = repository.findByName("spring2").get();
//        assertThat(member1).isEqualTo(result);
    }

    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        List<Member> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }
}
