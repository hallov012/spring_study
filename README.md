스프링 입문 - 코드로 배우는 스프링 부트, 웹 MVC, DB 접근 기술

## 빌드
1. `./gradlew build` or `./gradlew clean build`
   </br> clean: 이전 빌드 파일 삭제
2. `cd ./build/libs`
3. `java -jar hello-spring-0.0.1-SNAPSHOT.jar` </br>
   => 배포할 때, 서버에 jar 파일을 올리고 실행하면 된다.

## 스프링 웹 개발 기초
### 정적 컨텐츠
- `resources/static/hello-static.html` 파일 생성
- `http://localhost:8080/hello-static.html`로 접속 => 화면 확인 가능
- 스프링 부트는 기본적으로 정적 컨텐츠를 제공 해 줌 
![img.png](README_images/img.png)
- tomcat 서버가 브라우저에서 요청을 받으면 우선 Controller가 있는지 확인하고 없으면 static 폴더에서 찾는다.

### MVC와 템플릿 엔진
MVC: Model, View, Controller
- 예전엔 JSP를 가지고 Controller를 따로 두지 않고 View에서 모든 과정을 처리했지만, 현재는 Controller를 따로 두고 View에서는 화면을 그리는 역할만 한다. 
- 비지니스 로직이나 내부적인 것들은 거의 Model, Controller에서 처리한다.
</br>
</br>
**Controller**</br>
```java
@Controller
public class HelloController {
    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam("name") String name, Model model) {
        model.addAttribute("name", name);
        return "hello-template";
    }
}
```
**View**</br>
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<p th:text="'hello ' + ${name}">hello! empty</p>
</body>
</html>
```
예시 코드 `java/HelloSpringApplication.java` => hello-mvc</br>
![img_1.png](README_images/img_1.png)


### API
**@ResponseBody 문자 반환** </br>
```java
@Controller
public class HelloController {
    @GetMapping("hello-string")
    @ResponseBody
    public String helloString(@RequestParam("name") String name) {
        return "hello " + name;
    }
}
```

**@ResponseBody 사용 원리**</br>
![img_2.png](README_images/img_2.png)
* `@ResponseBody`를 사용
   * HTTP의 BODY에 문자 내용을 직접 반환
   * `viewResolver` 대신에 `HttpMessageConverter`가 동작
   * 기본 문자처리: `StringHttpMessageConverter`
   * 기본 객체처리: `MappingJackson2HttpMessageConverter`
   * byte 처리 등등 기타 여러 `HttpMessageConverter`가 기본으로 등록되어 있음

   > 참고: 클라이언트의 HTTP Accept 해더와 서버의 컨트롤러 반환 타입 정보 둘을 조합해서 `HttpMessageConverter`가 선택된다.
  
## 회원 관리 예제 - 백엔드 개발
### 비즈니스 요구사항 정리
- 데이터: 회원 ID, 이름
- 기능: 회원 등록, 조회

**앱 어플리케이션 계층 구조**</br>
![img_3.png](README_images/img_3.png)
- 컨트롤러: 웹 MVC의 컨트롤러 
- 서비스: 핵심 비즈니스 로직 구현 (ex. 중복 가입 제한)
- 리포지토리: 데이터베이스에 접근, 도메인 객체를 DB에 저장하고 관리
- 도메인: 비즈니스 도메인 객체
- 컨트롤러 -> 서비스 -> 리포지토리 -> DB

**클래스 의존관계**</br>
![img_4.png](README_images/img_4.png)
- 데이터 저장소가 선정되지 않아, 우선 인터페이스 구현 클래스를 변경할 수 있도록 설계

### 회원 도메인과 리포지토리 만들기
**회원객체**</br>
domain 패키지에 Member 클래스 생성
```java
public class Member {
    private Long id;
    private String name;
}
```
**회원 리포지토리**</br>
repository 패키지에 MemberRepository 인터페이스 생성
```java
public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);
    List<Member> findAll();
}
```

**회원 리포지토리 메모리 구현**</br>
repository 패키지에 MemoryMemberRepository 클래스 생성
```java
public class MemoryMemberRepository implements MemberRepository {
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }
}
```
### 테스트케이스 작성
개발한 기능을 실행해서 테스트 할 때, main 메소드를 실행하거나 웹 애플리케이션을 실행해서 테스트하는 것은 번거기에
반복실행이 어렵고, 여러 테스트를 한번에 실행하기 어렵다. 따라서 자바에서 **JUnit**이라는 프레임워크로 테스트를 실행해서 편리하게 테스트할 수 있다.
</br>
**테스트 코드 작성**</br>
`test > java > hello.hello_spring > repository > MemoryMemberRepositoryTest.java`
```java
class MemoryMemberRepositoryTest {
    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member();
        member.setName("spring");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();
        Assertions.assertEquals(member, result);
    }

    @Test
    public void findByName() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        Member result = repository.findByName("spring1").get();

        Assertions.assertEquals(member1, result);
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

        Assertions.assertEquals(2, result.size());
    }
}
```

### 회원 서비스 개발
서비스는 레파지토리와 도메인을 활용해서 비즈니스 로직을 처리한다.
</br>
**회원 서비스 기능 구현**</br>
`service > MemberService.java`
```java
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */
    public Long join(Member member) {
        // 같은 이름이 있는 중복 회원X
        validateDuplicateMember(member); // 중복 회원 검증
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

    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
```

### 회원 서비스 테스트
**테스트 코드 작성**</br>
`test > java > hello.hello_spring > service > MemberServiceTest.java`
```java
class MemberServiceTest {
    MemberService memberService;
    MemoryMemberRepository memberRepository;

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
        // given
        Member member = new Member();
        member.setName("hello");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        Assertions.assertEquals(member.getName(), findMember.getName());
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
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

        // then
        Assertions.assertEquals(e.getMessage(), "이미 존재하는 회원입니다.");
    }
}
```

## 스프링 빈과 의존관계
### 스프링 빈을 등록하고, 의존관계 설정하기
회원 컨트롤러가 회원 서비스와 회원 리포지토리를 사용할 수 있도록 의존관계를 설정한다.
**스프링 빈을 등록하는 2가지 방법**</br>
- 컴포넌트 스캔과 자동 의존관계 설정
- 자바 코드로 직접 스프링 빈 등록하기</br>

**컴포넌트 스캔과 자동 의존관계 설정**</br>
- `@Component` 어노테이션이 있으면 스프링 빈으로 자동 등록된다.
- `@Controller`, `@Service`, `@Repository`는 `@Component`를 포함하고 있으므로 스프링 빈으로 자동 등록된다.
- 메인 애플리케이션(`hello.hello_spring`)이 있는 패키지 하위에 있는 모든 컴포넌트를 스캔한다. (다른 곳에 등록된 경우는 스캔하지 않음)
```java
@Controller
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
}
```

![img_5.png](README_images/img_5.png)
* `memberService`와 `memberRepository`는 스프링 빈으로 등록되어 있어야 한다.
* `@Controller`, `@Service`, `@Repository` 등을 사용하면 스프링 빈으로 자동 등록된다.
> 참고: 스프링은 스프링 컨테이너에 스프링 빈을 등록할 때 싱글톤으로 등록한다. 따라서 같은 스프링 빈이면 모두 같은 인스턴스다. 설정으로 싱글톤이 아니게 설정할 수 있지만, 특별한 경우를 제외하면 대부분 싱글톤을 사용한다.</br> 

**자바 코드로 직접 스프링 빈 등록하기**</br>
- 회원 서비스와 회원 리포지토리를 직접 스프링 빈으로 등록하고, 의존관계를 설정한다.
- `@Service`, `@Repository`, `@Autowired`를 사용하지 않고 직접 설정한다.
```java
// hello.hello_spring.SpringConfig.java
package hello.hello_spring;

import hello.hello_spring.repository.MemberRepository;
import hello.hello_spring.repository.MemoryMemberRepository;
import hello.hello_spring.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
```
**예제의 경우 향후 메모리 리포지터리를 다른 리포지터리로 변경할 예정이마르, 컴포넌트 방식 대신 직접 스프링 빈으로 등록하는 방식을 사용한다.**
> 참고: DI(Dependency Injection)에는 필드 주입, setter 주입, 생성자 주입 이렇게 3가지 방식이 있다.</br>
> 의존관계가 실행 중에 동적으로 변하는 경우는 거의 업기 때문에 **생성자 주입**을 권장한다.</br>

> 참고: 실무에서는 주로 정형화된 컨트롤러, 서비스, 레파지토리 같은 코드는 컴포넌트 스캔을 사용한다. 그리고 정형화 되지 않거나, 상황에 따라 구현 클래스를 변경해야 하는 경우에는 설정을 통해 스프링 빈으로 등록한다.

> 참고: `@Autowired`를 통한 DI는 `hello.hello_spring` 패키지 이하에서만 동작한다. 따라서 `SpringConfig` 클래스를 만들어서 직접 스프링 빈을 등록하고, 의존관계를 설정한다.
