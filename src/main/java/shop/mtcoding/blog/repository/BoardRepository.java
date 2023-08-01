package shop.mtcoding.blog.repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.JoinDTO;
import shop.mtcoding.blog.dto.WriteDTO;

@Repository // 메모리에 띄운다. Autowired하면 메모리에 띄운걸 쓸수있다.
public class BoardRepository {

    @Autowired
    private EntityManager em;

    @Transactional // insert, update, delete 롤백과 커밋 자동으로 해줌
    public void save(WriteDTO writeDTO, Integer userID) {
        Query query = em.createNativeQuery(
                "insert into board_tb(title, content, user_id, created_at) values(:title, :content, :userId, now())");
        query.setParameter("title", writeDTO.getTitle());
        query.setParameter("content", writeDTO.getContent());
        query.setParameter("userId", userID);
        query.executeUpdate();
    }

}
