package shop.mtcoding.blog.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // localhost:8080/check?username=ssar
    @GetMapping("/check")
    public ResponseEntity<String> check(String username) {

        User user = userRepository.findByUsername(username);
        if (user != null) {
            return new ResponseEntity<String>("유저 네임이 중복되었습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("유저네임을 사용할 수 있습니다.", HttpStatus.OK);
    }

    @ResponseBody // 데이터 리턴
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

        User sessionUser = (User) session.getAttribute("sessionUser");
        System.out.println("테스트1");
        try {
            // 핵심 기능
            // user : DB에 있는 값
            User user = userRepository.findByUsername(loginDTO.getUsername());
            boolean isValid = BCrypt.checkpw(loginDTO.getPassword(), user.getPassword());
            if (isValid) {
                session.setAttribute("sessionUser", user);
                return "redirect:/"; // 로그인 된 페이지
            } else {
                return "redirect:/loginForm";
            }

        } catch (Exception e) {

            return "redirect:/exLogin"; // 아이디 또는 비밀번호 틀렸을 시
        }
    }

    // 실무
    @PostMapping("/join")
    public String join(JoinDTO joinDTO) { // JoinDTO에 값이 언제 담겼길래 joinDTO.getUsername()등을 할수 있냐?
        // 클라이언트가 /join을 때리면 view에서 action="/join"인 폼을 찾아 여기 body에 'key=value'형태로 담아서
        // request에 담아준다.
        // /join을 때린다고 해서 함수가 실행되진 않지만 Dispatcher Servlet이 자동으로 실행해주고, body에 담긴 key값을
        // join함수의 매개변수인
        // JoinDTO의 필드와 매칭시켜줘서 joinDTO에 담기게 된다.
        // DS가 하는 일은????

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

        // DB에 해당 username이 있는지 체크해보기
        User user = userRepository.findByUsername(joinDTO.getUsername());
        if (user != null) {
            return "redirect:/50x";
        }

        // JoinDTO로 받은 비밀번호를 BCrypt로 해시 해서 DB에 insert하기
        String encPassword = BCrypt.hashpw(joinDTO.getPassword(), BCrypt.gensalt());
        joinDTO.setPassword(encPassword);

        userRepository.save(joinDTO); // 핵심 기능
        return "redirect:/loginForm";

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
    public String updateForm(HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        User user = userRepository.findByUsername(sessionUser.getUsername()); // findByUsername은 pk가 아니니까 풀스캔해서
        // 느리다.(username은 unique이긴 함)
        // id는 index이므로 빠르다.
        // 귀찮으니까 username으로 찾는것
        request.setAttribute("user", user);

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
