package hello.hello_spring.repository;
import hello.hello_spring.domain.Member;
import java.util.Optional;
import java.util.List;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id); // null을 반환할 때 Optional로 감싸서 반환(java8)
    Optional<Member> findByName(String name);
    List<Member> findAll();
}