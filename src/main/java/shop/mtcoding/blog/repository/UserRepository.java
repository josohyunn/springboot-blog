package shop.mtcoding.blog.repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.JoinDTO;

// 현재 메모리에 떠있는것
// 직접 띄운것 : BoardController, UserController, UserRepository
// Spring에서 띄워준것 : EntityManager, HttpSession
@Repository // 얘를 붙여야지 메모리에 뜸(알아서 new해줌)
public class UserRepository {

    @Autowired
    private EntityManager em;

    @Transactional // insert, update, delete 롤백과 커밋 자동으로 해줌
    public void save(JoinDTO joinDTO) {
        Query query = em.createNativeQuery(
                "insert into user_tb(username, password, email) values(:username, :password, :email)");
        query.setParameter("username", joinDTO.getUsername());
        query.setParameter("password", joinDTO.getPassword());
        query.setParameter("email", joinDTO.getEmail());
        query.executeUpdate();
    }
}
