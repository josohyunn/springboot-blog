package shop.mtcoding.blog.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.qlrm.mapper.JpaResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.BoardDetailDTO;
import shop.mtcoding.blog.dto.JoinDTO;
import shop.mtcoding.blog.dto.UpdateDTO;
import shop.mtcoding.blog.dto.WriteDTO;
import shop.mtcoding.blog.model.Board;

@Repository // 메모리에 띄운다. Autowired하면 메모리에 띄운걸 쓸수있다.
public class BoardRepository {

    @Autowired
    private EntityManager em;

    // select id, title, from board_tb
    // resultClass 안붙이고 직접 파싱하려면!!
    // Object[]로 리턴됨.
    // object[0] = 1
    // object[1] = 제목1
    public int count() {
        // Entity(Board, User) 타입이 아니어도 기본자료형도 안된다.
        Query query = em.createNativeQuery("select count(*) from board_tb");
        BigInteger count = (BigInteger) query.getSingleResult();
        return (int) count.intValue();
    }

    public int count2() {
        // Entity(Board, User) 타입이 아니어도 기본자료형도 안된다.
        Query query = em.createNativeQuery("select * from board_tb", Integer.class);
        return (int) query.getMaxResults();
    }

    // localhost:8080?page=0
    public List<Board> findAll(int page) {
        final int SIZE = 3;
        // order by 보다 limit이 제일 뒤에 와야된다.
        Query query = em.createNativeQuery("select * from board_tb order by id desc limit :page, :size", Board.class); // 페이징
                                                                                                                       // 쿼리
        query.setParameter("page", page * SIZE);
        query.setParameter("size", SIZE);
        return query.getResultList();

    }

    @Transactional // insert, update, delete 롤백과 커밋 자동으로 해줌
    public void save(WriteDTO writeDTO, Integer userID) {
        Query query = em.createNativeQuery(
                "insert into board_tb(title, content, user_id, created_at) values(:title, :content, :userId, now())");
        query.setParameter("title", writeDTO.getTitle());
        query.setParameter("content", writeDTO.getContent());
        query.setParameter("userId", userID);
        query.executeUpdate();
    }

    // 동적 쿼리를 써도 되고 함수 2개를 써도 된다.
    public List<BoardDetailDTO> findByIdJoinReply(Integer boardId, Integer sessionUserId) {
        String sql = "select ";
        sql += "b.id board_id, ";
        sql += "b.content board_content, ";
        sql += "b.title board_title, ";
        sql += "b.user_id board_user_id, ";
        sql += "r.id reply_id, ";
        sql += "r.comment reply_comment, ";
        sql += "r.user_id reply_user_id, ";
        sql += "ru.username reply_user_username, ";
        if (sessionUserId == null) { // 동적 쿼리. 쿼리가 상황에 따라 달라진다.
            sql += "false reply_owner ";
        } else {
            sql += "case when r.user_id = :sessionUserId then true else false end reply_owner ";
        }

        sql += "from board_tb b left outer join reply_tb r ";
        sql += "on b.id = r.board_id ";
        sql += "left outer join user_tb ru ";
        sql += "on r.user_id = ru.id ";
        sql += "where b.id = :boardId ";
        sql += "order by r.id desc";
        Query query = em.createNativeQuery(sql);
        query.setParameter("boardId", boardId);
        if (sessionUserId != null) {
            query.setParameter("sessionUserId", sessionUserId);
        }

        JpaResultMapper mapper = new JpaResultMapper();
        List<BoardDetailDTO> dtos = mapper.list(query, BoardDetailDTO.class);
        return dtos;
    }

    public Board findById(Integer id) {
        Query query = em.createNativeQuery("select * from board_tb where id = :id", Board.class);
        query.setParameter("id", id);
        Board board = (Board) query.getSingleResult();
        return board;
    }

    @Transactional
    public void deleteById(Integer id) {
        Query query = em.createNativeQuery("delete from board_tb where id = :id", Board.class);
        query.setParameter("id", id);
        query.executeUpdate();

    }

    @Transactional
    public void update(UpdateDTO updateDTO, Integer id) {
        Query query = em.createNativeQuery("update board_tb set title = :title, content = :content where id = :id");
        query.setParameter("title", updateDTO.getTitle());
        query.setParameter("content", updateDTO.getContent());
        query.setParameter("id", id);
        query.executeUpdate();
    }

}
