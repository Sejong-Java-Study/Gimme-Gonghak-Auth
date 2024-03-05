package com.example.gimmegonghakauth.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.gimmegonghakauth.dao.MajorsDao;
import com.example.gimmegonghakauth.dao.UserDao;
import com.example.gimmegonghakauth.domain.MajorsDomain;
import com.example.gimmegonghakauth.domain.UserDomain;
import com.example.gimmegonghakauth.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties") //test용 DB
@Import(UserService.class)
@Nested
@DisplayName("DB 테스트(유저)")
public class UserServiceTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    private final MajorsDao majorsDao;
    private final UserDao userDao;
    private final UserService userService;

    @Autowired
    public UserServiceTest(MajorsDao majorsDao, UserDao userDao, UserService userService) {
        this.majorsDao = majorsDao;
        this.userDao = userDao;
        this.userService = userService;
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void testJoinUser() {
        MajorsDomain majorsDomain = MajorsDomain.builder().major("컴퓨터공학과").build();
        majorsDao.save(majorsDomain);

        UserDomain user = userService.create(
            19011684L
            , "1234"
            , "test@gmail.com"
            , majorsDao.findByMajor("컴퓨터공학과")
            , "testUser");

        userDao.save(user);
        assertEquals(user, userDao.findByStudentId(19011684L).get());
    }


}
