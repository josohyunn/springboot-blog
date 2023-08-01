package shop.mtcoding.blog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "user_tb") // 테이블 이름 지정
@Entity // 테이블 자동으로 만들어짐(ddl-auto : create이어야됨)
public class User {
    @Id // pk
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment

    private Integer id;

    @Column(nullable = false, length = 20, unique = true)
    private String username;
    @Column(nullable = false, length = 20)
    private String password;
    @Column(nullable = false, length = 20)
    private String email;

}
