package shop.mtcoding.blog.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
