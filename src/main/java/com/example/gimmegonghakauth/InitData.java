package com.example.gimmegonghakauth;

import com.example.gimmegonghakauth.constant.AbeekTypeConst;
import com.example.gimmegonghakauth.constant.CourseCategoryConst;
import com.example.gimmegonghakauth.dao.AbeekDao;
import com.example.gimmegonghakauth.dao.CompletedCoursesDao;
import com.example.gimmegonghakauth.dao.CoursesDao;
import com.example.gimmegonghakauth.dao.GonghakCorusesDao;
import com.example.gimmegonghakauth.dao.MajorsDao;
import com.example.gimmegonghakauth.dao.UserDao;
import com.example.gimmegonghakauth.domain.AbeekDomain;
import com.example.gimmegonghakauth.domain.AbeekDomain.AbeekDomainBuilder;
import com.example.gimmegonghakauth.domain.CompletedCoursesDomain;
import com.example.gimmegonghakauth.domain.CoursesDomain;
import com.example.gimmegonghakauth.domain.GonghakCoursesDomain;
import com.example.gimmegonghakauth.domain.MajorsDomain;
import com.example.gimmegonghakauth.domain.UserDomain;
import com.example.gimmegonghakauth.dto.GonghakRecommendCoursesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitData {

    private final MajorsDao majorsDao;
    private final AbeekDao abeekDao;
    private final CompletedCoursesDao completedCoursesDao;
    private final CoursesDao coursesDao;
    private final GonghakCorusesDao gonghakCorusesDao;
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initDatabase(){
        log.info("----set table----");
        MajorsDomain testMajorsDomain = MajorsDomain.builder()
            .id(1L)
            .major("컴퓨터공학과").build();
        log.info("----set table----");
        MajorsDomain testMajorsDomain2 = MajorsDomain.builder()
            .id(2L)
            .major("전자정보통신공학과").build();
        majorsDao.save(testMajorsDomain);
        majorsDao.save(testMajorsDomain2);

        //AbeekDomain
        AbeekDomainBuilder abeek1 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.MAJOR)
            .majorsDomain(testMajorsDomain)
            .note("this is a test note")
            .year(19)
            .minCredit(54);
        AbeekDomainBuilder abeek2 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.MSC)
            .majorsDomain(testMajorsDomain)
            .note("this is a test note")
            .year(19)
            .minCredit(30);
        AbeekDomainBuilder abeek3 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.DESIGN)
            .majorsDomain(testMajorsDomain)
            .note("this is a test note")
            .year(19)
            .minCredit(12);
        AbeekDomainBuilder abeek4 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.PROFESSIONAL_NON_MAJOR)
            .majorsDomain(testMajorsDomain)
            .note("this is a test note")
            .year(19)
            .minCredit(16);
        AbeekDomainBuilder abeek5 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.MINIMUM_CERTI)
            .majorsDomain(testMajorsDomain)
            .note("this is a test note")
            .year(19)
            .minCredit(100);

        abeekDao.save(abeek1.build());
        abeekDao.save(abeek2.build());
        abeekDao.save(abeek3.build());
        abeekDao.save(abeek4.build());
        abeekDao.save(abeek5.build());

        //User
        UserDomain userDomain = UserDomain.builder()
            .email("testEmail")
            .name("홍지섭")
            .password(passwordEncoder.encode("qwer"))
            .studentId(19011706L)
            .majorsDomain(testMajorsDomain).build();
        userDao.save(userDomain);

        UserDomain userDomain2 = UserDomain.builder()
            .email("testEmail")
            .name("testUser")
            .password(passwordEncoder.encode("1234"))
            .studentId(12345678L)
            .majorsDomain(testMajorsDomain).build();
        userDao.save(userDomain2);

        //Courses
        CoursesDomain testCourse1 = CoursesDomain.builder()
            .courseId(1234L)
            .credit(3)
            .name("testCourse1").build();
        CoursesDomain testCourse2 = CoursesDomain.builder()
            .courseId(2345L)
            .credit(4)
            .name("testCourse2").build();
        CoursesDomain testCourse3 = CoursesDomain.builder()
            .courseId(9000L)
            .credit(5)
            .name("testCourse3").build();
        CoursesDomain testCourse4 = CoursesDomain.builder()
            .courseId(9001L)
            .credit(3)
            .name("testCourse4").build();
        CoursesDomain testCourse5 = CoursesDomain.builder()
            .courseId(9002L)
            .credit(3)
            .name("testCourse5").build();
        coursesDao.save(testCourse1);
        coursesDao.save(testCourse2);
        coursesDao.save(testCourse3);
        coursesDao.save(testCourse4);
        coursesDao.save(testCourse5);

        //CompletedCourses
        CompletedCoursesDomain coursesDomain1 = CompletedCoursesDomain.builder()
            .year(2019)
            .semester(1)
            .coursesDomain(testCourse1)
            .userDomain(userDomain).build();
        CompletedCoursesDomain coursesDomain2 = CompletedCoursesDomain.builder()
            .year(2019)
            .semester(1)
            .coursesDomain(testCourse2)
            .userDomain(userDomain).build();
        CompletedCoursesDomain coursesDomain3 = CompletedCoursesDomain.builder()
            .year(2019)
            .semester(1)
            .coursesDomain(testCourse3)
            .userDomain(userDomain).build();
        CompletedCoursesDomain coursesDomain4 = CompletedCoursesDomain.builder()
            .year(2019)
            .semester(1)
            .coursesDomain(testCourse4)
            .userDomain(userDomain).build();

        completedCoursesDao.save(coursesDomain1);
        completedCoursesDao.save(coursesDomain2);
        completedCoursesDao.save(coursesDomain3);
        completedCoursesDao.save(coursesDomain4);

        //gonghakCourses
        GonghakCoursesDomain gonghakCourses1 = GonghakCoursesDomain.builder()
            .courseCategory(CourseCategoryConst.MSC)
            .majorsDomain(testMajorsDomain)
            .designCredit(0.0)
            .coursesDomain(testCourse1)
            .passCategory("인필")
            .year(19).build();

        GonghakCoursesDomain gonghakCourses2 = GonghakCoursesDomain.builder()
            .courseCategory(CourseCategoryConst.MSC)
            .majorsDomain(testMajorsDomain)
            .designCredit(0.0)
            .coursesDomain(testCourse2)
            .passCategory("인필")
            .year(19).build();

        GonghakCoursesDomain gonghakCourses3 = GonghakCoursesDomain.builder()
            .courseCategory(CourseCategoryConst.MAJOR_SELECTIVE)
            .majorsDomain(testMajorsDomain)
            .designCredit(1.0)
            .coursesDomain(testCourse3)
            .passCategory("인선")
            .year(19).build();

        GonghakCoursesDomain gonghakCourses4 = GonghakCoursesDomain.builder()
            .courseCategory(CourseCategoryConst.MAJOR_REQUIRED)
            .majorsDomain(testMajorsDomain)
            .designCredit(1.0)
            .coursesDomain(testCourse5)
            .passCategory("인선")
            .year(19).build();

        gonghakCorusesDao.save(gonghakCourses1);
        gonghakCorusesDao.save(gonghakCourses2);
        gonghakCorusesDao.save(gonghakCourses3);
        gonghakCorusesDao.save(gonghakCourses4);
    }
}
