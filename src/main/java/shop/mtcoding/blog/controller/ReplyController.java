package shop.mtcoding.blog.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import shop.mtcoding.blog.dto.ReplyWriteDTO;
import shop.mtcoding.blog.model.User;
import shop.mtcoding.blog.repository.ReplyRepository;

@Controller
public class ReplyController {

    @Autowired
    private ReplyRepository replyRepository; // 만들기

    @Autowired
    private HttpSession session;

    @PostMapping("/reply/save")
    public String save(ReplyWriteDTO replyWriteDTO) {
        // comment 유효성 검사 - null인지 공백인지
        if (replyWriteDTO.getBoardId() == null) {
            return "redirect:/40x";
        }

        if (replyWriteDTO.getComment() == null || replyWriteDTO.getComment().isEmpty()) {
            return "redirect:/40x";
        }

        // 인증 검사 - session값 없으면 로그인페이지, 있으면 밑에 userId넘겨주기
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 댓글 쓰기
        replyRepository.save(replyWriteDTO, sessionUser.getId());
        System.out.println("테스트댓글 : " + replyWriteDTO.getComment());
        System.out.println("테스트아이디 : " + sessionUser.getId());
        System.out.println("테스트게시글 : " + replyWriteDTO.getBoardId());

        return "rdeirect:/board/" + replyWriteDTO.getBoardId();
    }
}
