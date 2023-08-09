package shop.mtcoding.blog.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.ReplyWriteDTO;
import shop.mtcoding.blog.dto.WriteDTO;
import shop.mtcoding.blog.model.Reply;

// 메모리에 떠있는것
// 직접 : UserController, BoardController, ReplyController, ErrorController
// 직접 : UserRepository, BoardRepository, ReplyRepository
// Spring에서 : EntityManager, HttpSession
@Repository
public class ReplyRepository {

    @Autowired
    private EntityManager em;

    public Reply findById(int id) {
        // query문 뒤에 .Reply.class는 오브젝트 매핑을 해주는 것
        // query문의 컬럼들을 Reply클래스에 매핑해주는 것
        Query query = em.createNativeQuery("select * from reply_tb where id = :id", Reply.class);
        query.setParameter("id", id);
        return (Reply) query.getSingleResult();
    }

    public List<Reply> findByBoardId(Integer boardId) {
        Query query = em.createNativeQuery("select * from reply_tb where board_id = :boardId", Reply.class);
        query.setParameter("boardId", boardId);
        return query.getResultList(); // List는 다운캐스팅 안해도된다
    }

    @Transactional
    public void save(ReplyWriteDTO replyWriteDTO, Integer userId) {

        Query query = em.createNativeQuery(
                "insert into reply_tb(comment, user_id, board_id) values(:comment, :userId, :boardId)");
        query.setParameter("comment", replyWriteDTO.getComment());
        query.setParameter("userId", userId);
        query.setParameter("boardId", replyWriteDTO.getBoardId());
        query.executeUpdate(); // 쿼리 전송
    }

    @Transactional // db변경할 거기 때문에 사용
    public void deleteById(Integer id) {

        Query query = em.createNativeQuery(
                "delete from reply_tb where id = :id");
        query.setParameter("id", id);
        query.executeUpdate(); // 쿼리 전송
    }

}
