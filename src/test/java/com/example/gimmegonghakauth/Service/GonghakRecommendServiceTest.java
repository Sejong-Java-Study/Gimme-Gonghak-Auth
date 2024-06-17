package com.example.gimmegonghakauth.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.gimmegonghakauth.constant.AbeekTypeConst;
import com.example.gimmegonghakauth.dao.UserDao;
import com.example.gimmegonghakauth.domain.UserDomain;
import com.example.gimmegonghakauth.dto.IncompletedCoursesDto;
import com.example.gimmegonghakauth.dto.LoginDto;
import com.example.gimmegonghakauth.service.recommend.ComputerMajorGonghakRecommendService;
import com.example.gimmegonghakauth.service.recommend.GonghakRecommendService;
import com.example.gimmegonghakauth.service.recommend.RecommendServiceSelectManager;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class GonghakRecommendServiceTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RecommendServiceSelectManager recommendServiceSelectManager;

    private static final Long COM_TEST_STUDENT_ID = 19011706L;

    @Test
    @DisplayName("유저의 학과에 맞는 recommendService 클래스 가져오기 확인 - 컴퓨터공학과")
    void recommendServiceSelectManagerTest(){
        LoginDto loginDto = new LoginDto(COM_TEST_STUDENT_ID);
        GonghakRecommendService gonghakRecommendService = recommendServiceSelectManager.selectRecommendService(
            loginDto.getStudentId());
        assertThat(gonghakRecommendService).isInstanceOf(
            ComputerMajorGonghakRecommendService.class);
    }

    @Test
    void createRecommendCoursesTest(){
        UserDomain testUser = userDao.findByStudentId(COM_TEST_STUDENT_ID).get();

        GonghakRecommendService comGonghakRecommendService = recommendServiceSelectManager.selectRecommendService(
            new LoginDto(COM_TEST_STUDENT_ID).getStudentId());
        Map<AbeekTypeConst, List<IncompletedCoursesDto>> recommendCoursesByAbeekType = comGonghakRecommendService.createRecommendCourses(
            testUser).getRecommendCoursesByAbeekType();

        log.info("recommendCoursesByAbeekType.keySet()= {}",recommendCoursesByAbeekType.keySet());
        //[PROFESSIONAL_NON_MAJOR, BSM, DESIGN, MAJOR, MINIMUM_CERTI]

        recommendCoursesByAbeekType.keySet().forEach(
            abeekTypeConst -> {
                log.info("abeekTypeConst.name() = {}",abeekTypeConst.name());
                recommendCoursesByAbeekType.get(abeekTypeConst);
                for (IncompletedCoursesDto incompletedCoursesDto : recommendCoursesByAbeekType.get(abeekTypeConst)) {
                    log.info("incompletedCoursesDto.getCourseName= {}",incompletedCoursesDto.getCourseName());
                }
            }
        );

        assertThat(recommendCoursesByAbeekType.get(AbeekTypeConst.PROFESSIONAL_NON_MAJOR).size()).isNotEqualTo(0);

        assertThat(recommendCoursesByAbeekType.keySet()).containsOnly(
            AbeekTypeConst.PROFESSIONAL_NON_MAJOR,
            AbeekTypeConst.BSM,
            AbeekTypeConst.DESIGN,
            AbeekTypeConst.MAJOR,
            AbeekTypeConst.MINIMUM_CERTI
        );
    }
}
