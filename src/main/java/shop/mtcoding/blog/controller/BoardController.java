package shop.mtcoding.blog.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.mtcoding.blog.dto.UpdateDTO;
import shop.mtcoding.blog.dto.WriteDTO;
import shop.mtcoding.blog.model.Board;
import shop.mtcoding.blog.model.User;
import shop.mtcoding.blog.repository.BoardRepository;

@Controller
public class BoardController { // 요청할 때에는 항상 controller로

    @Autowired
    private HttpSession session;

    @Autowired
    private BoardRepository boardRepository;

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable Integer id, UpdateDTO updateDTO) {
        boardRepository.update(updateDTO, id);

        return "redirect:/board/" + id;
    }

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable Integer id, HttpServletRequest request) {
        // 1. 인증 검사

        // 2. 권한 체크

        // 3. 핵심 로직
        Board board = boardRepository.findById(id);
        request.setAttribute("board", board);

        return "board/updateForm";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Integer id) { // 1. PathVariable 값 받기
        // 2. 인증 검사(로그인 페이지 보내기)
        // session에 접근해서 sessionUser 키값을 가져오세요
        // null이면 로그인 페이지로 보내고
        // null이 아니면 3번을 실행하세요.
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm"; // 401
        }

        // 3. 권한 검사
        Board board = boardRepository.findById(id);
        if (board.getUser().getId() != sessionUser.getId()) {
            return "redirect:/40x"; // 403 권한 없음
        }

        // 4. 모델에 접근해서 삭제
        // boardRepository.deleteById(id); 호출하세요 -> 리턴을 받지 마세요
        // delete from board_tb where id=:id
        boardRepository.deleteById(id);

        return "redirect:/";
    }

    @GetMapping({ "/", "/board" }) // 주소 두개 매핑
    // page = 0으로 초기화시켜줌
    public String index(@RequestParam(defaultValue = "0") Integer page, HttpServletRequest request) {

        // 1. 유효성 검사 x(post가 아니기때문에 받을게 없다)
        // 2. 인증검사 x(로그인 안해도 볼 수 있음)

        List<Board> boardList = boardRepository.findAll(page); // page = 1
        int totalCount = boardRepository.count(); // totalCount = 5
        int totalPage = totalCount / 3; // totalPage = 1
        if (totalCount % 3 > 0) {
            totalPage = totalPage + 1; // totalPage = 2
        }
        boolean last = totalPage - 1 == page;

        // System.out.println("테스트 : " + boardList.size());
        // System.out.println("테스트 : " + boardList.get(0).getTitle());

        request.setAttribute("boardList", boardList);
        request.setAttribute("prevPage", page - 1);
        request.setAttribute("nextPage", page + 1);
        request.setAttribute("first", page == 0 ? true : false);
        request.setAttribute("last", last);
        request.setAttribute("totalPage", totalPage);
        request.setAttribute("totalCount", totalCount);

        return "index";
    }

    @PostMapping("/board/save")
    public String save(WriteDTO writeDTO) {
        // validation check(유효성 검사)
        if (writeDTO.getTitle() == null || writeDTO.getTitle().isEmpty()) {
            return "redirect:/40x";
        }
        if (writeDTO.getContent() == null || writeDTO.getContent().isEmpty()) {
            return "redirect:/40x";
        }

        // 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        boardRepository.save(writeDTO, sessionUser.getId()); // 핵심 로직
        return "redirect:/";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        // 부가 로직
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }
        return "board/saveForm"; // 핵심 로직
    }

    // localhost:8080/board/1
    // localhost:8080/board/50
    @GetMapping("/board/{id}")
    public String detail(@PathVariable Integer id, HttpServletRequest request) { // C
        User sessionUser = (User) session.getAttribute("sessionUser");
        Board board = boardRepository.findById(id); // M
        boolean pageOwner = false;

        if (sessionUser != null) {
            // 페이지 만든사람 id와 현재 접속되어있는 세션id가 같은 사람이면 수정/삭제 가능
            pageOwner = sessionUser.getId() == board.getUser().getId();
        }
        request.setAttribute("board", board);
        request.setAttribute("pageOwner", pageOwner);

        return "board/detail"; // V
    }
}
