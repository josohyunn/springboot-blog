package shop.mtcoding.blog;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptTest {
    public static void main(String[] args) {
        String encPassword = BCrypt.hashpw("1234", BCrypt.gensalt());
        System.out.println("encPassword : " + encPassword);

        boolean isValid = BCrypt.checkpw("1234", encPassword);
        System.out.println(isValid);
    }

}
// DB에는 암호화된 것(encPassword)부터 넣어본다.
// 더미데이터도 생각해보기