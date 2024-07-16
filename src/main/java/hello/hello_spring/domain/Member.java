package hello.hello_spring.domain;

import jakarta.persistence.*;

@Entity // JPA가 관리하는 Entity
public class Member {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 알아서 생성해주는 것
    private Long id; // 시스템이 정하는 ID

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
