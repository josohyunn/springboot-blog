package shop.mtcoding.blog.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import shop.mtcoding.blog.dto.JoinDTO;
import shop.mtcoding.blog.dto.LoginDTO;
import shop.mtcoding.blog.model.User;
import shop.mtcoding.blog.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session; // request는 가방, session은 서랍

    @ResponseBody
    @GetMapping("/test/login")
    public String testLogin() {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "로그인이 되지 않았습니다";
        } else {
            return "로그인 됨 : " + sessionUser.getUsername();
        }
    }

    @PostMapping("/login")
    public String login(LoginDTO loginDTO) {
        // validation check(유효성 검사)
        if (loginDTO.getUsername() == null || loginDTO.getUsername().isEmpty()) {
            return "redirect:/40x";
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
            return "redirect:/40x";
        }

        try {
            // 핵심 기능
            User user = userRepository.findByUsernameAndPassword(loginDTO);
            session.setAttribute("sessionUser", user);
            return "redirect:/"; // 로그인 된 페이지
        } catch (Exception e) {

            return "redirect:/exLogin"; // 아이디 또는 비밀번호 틀렸을 시
        }
    }

    // 실무
    @PostMapping("/join")
    public String join(JoinDTO joinDTO) {

        // validation check(유효성 검사)
        // 프론트엔드로 접근하는 사람 말고 비정상적인 postman등으로 접근하는 사람을 막기 위해 작성
        // redirection을 다른곳으로 보내줌
        if (joinDTO.getUsername() == null || joinDTO.getUsername().isEmpty()) {
            return "redirect:/40x";
        }
        if (joinDTO.getPassword() == null || joinDTO.getPassword().isEmpty()) {
            return "redirect:/40x";
        }
        if (joinDTO.getEmail() == null || joinDTO.getEmail().isEmpty()) {
            return "redirect:/40x";
        }

        try {
            userRepository.save(joinDTO); // 핵심 기능
            return "redirect:/loginForm";
        } catch (Exception e) {
            return "redirect:/50x";
        }

    }

    // ==========================================================
    // 약간 정상
    // DS(컨트롤러 메서드 찾기, 바디데이터 파싱)
    // DS가 바디데이터를 파싱안하고 컨트롤러 메서드만 찾은 상황
    // @PostMapping("/join")
    // public String join(HttpServletRequest request) {
    // String username = request.getParameter("username");
    // String password = request.getParameter("password");
    // String email = request.getParameter("email");
    // System.out.println("username: " + username);
    // System.out.println("password: " + password);
    // System.out.println("email: " + email);
    // return "redirect:/loginForm";
    // }

    // 비정상
    // @PostMapping("/join")
    // public String join(HttpServletRequest request) throws IOException {
    // // username=ssar&password=1234&email=ssar@nate.com
    // BufferedReader br = request.getReader();

    // // 버퍼가 소비됨
    // String body = br.readLine();

    // // 버퍼에 값이 없어서 못꺼냄(앞에서 소비되었기 때문)
    // String username = request.getParameter("username");

    // System.out.println("body: " + body);
    // System.out.println("username: " + username);

    // return "redirect:/loginForm";
    // }

    // 정상인(잘 안씀)
    // @PostMapping("/join")
    // public String join(String username, String password, String email) {
    // System.out.println("username: " + username);
    // System.out.println("password: " + password);
    // System.out.println("email: " + email);
    // return "redirect:/loginForm";
    // }
    // ==========================================================

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    // localhost:8080/user/updateForm 요청하면
    // user.updateForm 실행됨
    @GetMapping("/user/updateForm") // 얘를 때리는것.(return 때리는거 아님)
    public String updateForm() {
        // templates/ -> prefix
        // .mustache -> postfix
        return "user/updateForm"; // ViewResolver -> templates패키지 찾음 -> 실행됨
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 내 세션과 관련된 것을 다 날려버린다.
        // 세션 무효화(내 서랍을 비우는 것)
        return "redirect:/";
    }

}
