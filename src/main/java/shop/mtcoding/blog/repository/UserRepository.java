package shop.mtcoding.blog.repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.JoinDTO;
import shop.mtcoding.blog.dto.LoginDTO;
import shop.mtcoding.blog.model.User;

// 현재 메모리에 떠있는것
// 직접 띄운것 : BoardController, UserController, UserRepository
// Spring에서 띄워준것 : EntityManager, HttpSession
@Repository // 얘를 붙여야지 메모리에 뜸(알아서 new해줌)
public class UserRepository {

    @Autowired
    private EntityManager em;

    String encPassword = null;

    public User findByUsername(String username) {
        try {
            Query query = em.createNativeQuery("select * from user_tb where username=:username",
                    User.class); // 매핑을 하는 이유 : return을 해야 하기 때문에
            // 클래스로 받는 이유 : 반환해야 하는 컬럼 갯수가 1개이면 변수로 해도 되는데, select * 이기 때문에 여러 개의 컬럼을 반환해야
            // 한다.
            // 그래서 클래스로 받아서 클래스로 return을 해줘야 한다.
            query.setParameter("username", username);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            return null; // 찾은 행이 없을 때
        }

    }

    public User findByUsernameAndPassword(LoginDTO loginDTO) {
        Query query = em.createNativeQuery("select * from user_tb where username=:username and password=:password",
                User.class);
        query.setParameter("username", loginDTO.getUsername());
        query.setParameter("password", loginDTO.getPassword());

        return (User) query.getSingleResult();
    }

    @Transactional // insert, update, delete 롤백과 커밋 자동으로 해줌
    public void save(JoinDTO joinDTO) {
        Query query = em.createNativeQuery(
                "insert into user_tb(username, password, email) values(:username, :password, :email)");
        query.setParameter("username", joinDTO.getUsername());
        query.setParameter("password", joinDTO.getPassword());
        query.setParameter("email", joinDTO.getEmail());
        query.executeUpdate(); // 쿼리를 전송(DBMS)
    }
}
