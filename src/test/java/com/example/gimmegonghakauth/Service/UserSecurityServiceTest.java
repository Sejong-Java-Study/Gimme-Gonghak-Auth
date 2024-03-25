package com.example.gimmegonghakauth.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.gimmegonghakauth.dao.MajorsDao;
import com.example.gimmegonghakauth.dao.UserDao;
import com.example.gimmegonghakauth.domain.MajorsDomain;
import com.example.gimmegonghakauth.domain.UserDomain;
import com.example.gimmegonghakauth.domain.UserRole;
import com.example.gimmegonghakauth.service.CompletedCoursesService;
import com.example.gimmegonghakauth.service.UserSecurityService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;


@Nested
@Transactional
@DisplayName("DB 테스트(로그인)")
public class UserSecurityServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserSecurityService userSecurityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("로그인 성공(관리자)")
    public void testLoadUserByUsernameSuccess1() {
        Long studentId = 19011684L;
        MajorsDomain major = MajorsDomain.builder().major("컴퓨터공학과").build();

        UserDomain mockUser = UserDomain.builder()
            .studentId(studentId).password("1234").email("1234@naver.com")
            .majorsDomain(major).name("test")
            .build();
        when(userDao.findByStudentId(studentId)).thenReturn(Optional.of(mockUser));
        //Mock User 생성

        // When
        UserDetails userDetails = userSecurityService.loadUserByUsername(studentId.toString());

        // Then
        assertEquals("19011684", userDetails.getUsername());
        assertEquals("1234", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());  // 하나의 권한(롤)을 예상
        assertEquals(UserRole.ADMIN.getValue(),
            userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("로그인 성공(일반 사용자)")
    public void testLoadUserByUsernameSuccess2() {
        Long studentId = 19011685L;
        MajorsDomain major = MajorsDomain.builder().major("컴퓨터공학과").build();

        UserDomain mockUser = UserDomain.builder()
            .studentId(studentId).password("1234").email("1234@naver.com")
            .majorsDomain(major).name("test")
            .build();
        when(userDao.findByStudentId(studentId)).thenReturn(Optional.of(mockUser));
        //Mock User 생성

        // When
        UserDetails userDetails = userSecurityService.loadUserByUsername(studentId.toString());

        // Then
        assertEquals("19011685", userDetails.getUsername());
        assertEquals("1234", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());  // 하나의 권한(롤)을 예상
        assertEquals(UserRole.USER.getValue(),
            userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("로그인 실패 (사용자 없음)")
    public void testLoadUserByUsernameFailure() {
        // Given
        Long studentId = 123456L;
        when(userDao.findByStudentId(studentId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(UsernameNotFoundException.class, () ->
            userSecurityService.loadUserByUsername(studentId.toString()));
    }

}
