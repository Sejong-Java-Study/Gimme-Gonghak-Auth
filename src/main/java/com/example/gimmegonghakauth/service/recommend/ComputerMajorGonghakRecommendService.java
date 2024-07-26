package com.example.gimmegonghakauth.service.recommend;


import com.example.gimmegonghakauth.constant.AbeekTypeConst;
import com.example.gimmegonghakauth.constant.CourseCategoryConst;
import com.example.gimmegonghakauth.dao.GonghakRepository;
import com.example.gimmegonghakauth.domain.UserDomain;
import com.example.gimmegonghakauth.dto.GonghakRecommendCoursesDto;
import com.example.gimmegonghakauth.dto.GonghakStandardDto;
import com.example.gimmegonghakauth.dto.IncompletedCoursesDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComputerMajorGonghakRecommendService implements GonghakRecommendService {
    private final GonghakRepository gonghakRepository;

    //리팩토링 필요
    @Override
    @Transactional(readOnly = true)
    public GonghakRecommendCoursesDto createRecommendCourses(UserDomain userDomain){
        GonghakRecommendCoursesDto gonghakRecommendCoursesDto = new GonghakRecommendCoursesDto();

        // findStandard -> 학번 입학년도를 기준으로 해당 년도의 abeekType(영역별 구분),minCredit(영역별 인증학점) 불러온다.
        Optional<GonghakStandardDto> standard = gonghakRepository.findStandard(
            userDomain.getStudentId(), userDomain.getMajorsDomain());

        // 수강하지 않은 과목 중 "전공기초" 과목을 반환한다.
        List<IncompletedCoursesDto> majorBasic = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
            CourseCategoryConst.전공기초, userDomain.getStudentId(), userDomain.getMajorsDomain()
        );
        printLog(majorBasic);

        // 수강하지 않은 과목 중 "전공주제" 과목을 반환한다.
        List<IncompletedCoursesDto> majorSubject = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
            CourseCategoryConst.전공주제, userDomain.getStudentId(), userDomain.getMajorsDomain()
        );
        printLog(majorSubject);

        // 수강하지 않은 과목 중 "전문교양" 과목을 반환한다.
        List<IncompletedCoursesDto> nonMajor = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
            CourseCategoryConst.전문교양, userDomain.getStudentId(), userDomain.getMajorsDomain()
        );
        printLog(nonMajor);

        // 수강하지 않은 과목 중 "BSM" 과목을 반환한다.
        List<IncompletedCoursesDto> bsm = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
            CourseCategoryConst.BSM, userDomain.getStudentId(), userDomain.getMajorsDomain()
        );
        printLog(bsm);

        // abeekType 별 추천 과목 List를 반환한다.
        Map<AbeekTypeConst, List<IncompletedCoursesDto>> coursesByAbeekTypeWithoutCompleteCourses = gonghakRecommendCoursesDto.getRecommendCoursesByAbeekType();
        Arrays.stream(AbeekTypeConst.values()).forEach(
            abeekType -> {
                List<IncompletedCoursesDto> abeekRecommend = new ArrayList<>();
                if(standard.get().getStandards().containsKey(abeekType)){
                    switch (abeekType){
                        case BSM:
                            abeekRecommend.addAll(bsm); break;
                        case MAJOR:
                            abeekRecommend.addAll(majorBasic);
                            abeekRecommend.addAll(majorSubject); break;
                        case DESIGN:
                            addOnlyDesignCreditOverZero(majorBasic, abeekRecommend);
                            addOnlyDesignCreditOverZero(majorSubject, abeekRecommend);
                            break;
                        case PROFESSIONAL_NON_MAJOR:
                            abeekRecommend.addAll(nonMajor); break;
                        case MINIMUM_CERTI:
                            abeekRecommend.addAll(bsm);
                            abeekRecommend.addAll(majorBasic);
                            abeekRecommend.addAll(majorSubject);
                            abeekRecommend.addAll(nonMajor); break;
                    }
                    coursesByAbeekTypeWithoutCompleteCourses.put(abeekType,abeekRecommend);
                }


            }
        );

        return gonghakRecommendCoursesDto;
    }

    // 설계 과목(designCredit > 0)인 경우만 추가한다.
    private static void addOnlyDesignCreditOverZero(List<IncompletedCoursesDto> majorBasic,
        List<IncompletedCoursesDto> abeekRecommend) {
        majorBasic.forEach(
            incompletedCoursesDto -> {
                if(incompletedCoursesDto.getDesignCredit()>0){
                    abeekRecommend.add(incompletedCoursesDto);
                }
            }
        );
    }

    private static void printLog(List<IncompletedCoursesDto> incompletedCoursesDtoList){
        log.info("dto list size = {}",incompletedCoursesDtoList.size());
        for (IncompletedCoursesDto incompletedCoursesDto : incompletedCoursesDtoList) {
            log.info("courseName = {}",incompletedCoursesDto.getCourseName());
        }
    }
}
