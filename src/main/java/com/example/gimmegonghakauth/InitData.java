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
import com.example.gimmegonghakauth.service.recommend.MajorName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!(prod || release)")
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
        MajorsDomain computerMajor = MajorsDomain.builder()
            .id(1L)
            .major(MajorName.COMPUTER.getName()).build();
        log.info("----set table----");
        MajorsDomain elecInfoMajor = MajorsDomain.builder()
            .id(2L)
            .major(MajorName.ELEC_INFO.getName()).build();
        majorsDao.save(computerMajor);
        majorsDao.save(elecInfoMajor);

        //19학년도 computerMajor
        AbeekDomainBuilder abeek1 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.BSM)
            .majorsDomain(computerMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(18);
        AbeekDomainBuilder abeek2 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.PROFESSIONAL_NON_MAJOR)
            .majorsDomain(computerMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(14);
        AbeekDomainBuilder abeek3 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.DESIGN)
            .majorsDomain(computerMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(12);
        AbeekDomainBuilder abeek4 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.MAJOR)
            .majorsDomain(computerMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(60);
        AbeekDomainBuilder abeek5 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.MINIMUM_CERTI)
            .majorsDomain(computerMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(92);

        abeekDao.save(abeek1.build());
        abeekDao.save(abeek2.build());
        abeekDao.save(abeek3.build());
        abeekDao.save(abeek4.build());
        abeekDao.save(abeek5.build());

        //19학년도 전정통
        AbeekDomainBuilder abeek21 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.MSC)
            .majorsDomain(elecInfoMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(30);
        AbeekDomainBuilder abeek22 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.PROFESSIONAL_NON_MAJOR)
            .majorsDomain(elecInfoMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(14);
        AbeekDomainBuilder abeek23 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.DESIGN)
            .majorsDomain(elecInfoMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(9);
        AbeekDomainBuilder abeek24 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.MAJOR)
            .majorsDomain(elecInfoMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(54);
        AbeekDomainBuilder abeek25 = AbeekDomain.builder()
            .abeekType(AbeekTypeConst.MINIMUM_CERTI)
            .majorsDomain(elecInfoMajor)
            .note("this is a test note")
            .year(19)
            .minCredit(98);

        abeekDao.save(abeek21.build());
        abeekDao.save(abeek22.build());
        abeekDao.save(abeek23.build());
        abeekDao.save(abeek24.build());
        abeekDao.save(abeek25.build());

        //User
        UserDomain userDomain = UserDomain.builder()
            .email("testEmail@sju.ac.kr")
            .name("홍지섭")
            .password(passwordEncoder.encode("qwer"))
            .studentId(19011706L)
            .majorsDomain(computerMajor).build();
        userDao.save(userDomain);

        UserDomain userDomainElec = UserDomain.builder()
            .email("testEmail123@sju.ac.kr")
            .name("전통이")
            .password(passwordEncoder.encode("qwer"))
            .studentId(19111111L)
            .majorsDomain(elecInfoMajor).build();
        userDao.save(userDomainElec);

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
            .semester("1학기")
            .coursesDomain(testCourse1)
            .userDomain(userDomain).build();
        CompletedCoursesDomain coursesDomain2 = CompletedCoursesDomain.builder()
            .year(2019)
            .semester("1학기")
            .coursesDomain(testCourse2)
            .userDomain(userDomain).build();
        CompletedCoursesDomain coursesDomain3 = CompletedCoursesDomain.builder()
            .year(2019)
            .semester("1학기")
            .coursesDomain(testCourse3)
            .userDomain(userDomain).build();
        CompletedCoursesDomain coursesDomain4 = CompletedCoursesDomain.builder()
            .year(2019)
            .semester("1학기")
            .coursesDomain(testCourse4)
            .userDomain(userDomain).build();

        completedCoursesDao.save(coursesDomain1);
        completedCoursesDao.save(coursesDomain2);
        completedCoursesDao.save(coursesDomain3);
        completedCoursesDao.save(coursesDomain4);

        //gonghakCourses
        GonghakCoursesDomain gonghakCourses1 = GonghakCoursesDomain.builder()
            .courseCategory(CourseCategoryConst.BSM)
            .majorsDomain(computerMajor)
            .designCredit(0.0)
            .coursesDomain(testCourse1)
            .passCategory("인필")
            .year(19).build();

        GonghakCoursesDomain gonghakCourses2 = GonghakCoursesDomain.builder()
            .courseCategory(CourseCategoryConst.BSM)
            .majorsDomain(computerMajor)
            .designCredit(0.0)
            .coursesDomain(testCourse2)
            .passCategory("인필")
            .year(19).build();

        GonghakCoursesDomain gonghakCourses3 = GonghakCoursesDomain.builder()
            .courseCategory(CourseCategoryConst.전선)
            .majorsDomain(computerMajor)
            .designCredit(1.0)
            .coursesDomain(testCourse3)
            .passCategory("인선")
            .year(19).build();

        GonghakCoursesDomain gonghakCourses4 = GonghakCoursesDomain.builder()
            .courseCategory(CourseCategoryConst.전필)
            .majorsDomain(computerMajor)
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
