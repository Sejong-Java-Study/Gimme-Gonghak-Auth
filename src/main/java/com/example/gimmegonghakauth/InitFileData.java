package com.example.gimmegonghakauth;

import com.example.gimmegonghakauth.constant.CourseCategoryConst;
import com.example.gimmegonghakauth.dao.CoursesDao;
import com.example.gimmegonghakauth.dao.GonghakCorusesDao;
import com.example.gimmegonghakauth.dao.MajorsDao;
import com.example.gimmegonghakauth.domain.CoursesDomain;
import com.example.gimmegonghakauth.domain.GonghakCoursesDomain;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Profile("!(prod || release)")
public class InitFileData {

    private final MajorsDao majorsDao;
    private final CoursesDao coursesDao;
    private final GonghakCorusesDao gonghakCorusesDao;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadCoursesDataFromCSV() throws IOException {
        String csvFilePath = "src/main/java/com/example/gimmegonghakauth/19학년2학기_20학년1학기_컴공.csv";
        inputCoursesCsv(csvFilePath);
        csvFilePath = "src/main/java/com/example/gimmegonghakauth/19학년2학기_20학년1학기_전정통.csv";
        inputCoursesCsv(csvFilePath);
        csvFilePath = "src/main/java/com/example/gimmegonghakauth/19학년2학기_20학년1학기_대양.csv";
        inputCoursesCsv(csvFilePath);

        csvFilePath = "src/main/java/com/example/gimmegonghakauth/computerMajorGonghakCourses2019Test.csv";
        inputGonghakCoursesCsv(csvFilePath);
        csvFilePath = "src/main/java/com/example/gimmegonghakauth/elecInfoMajorGonghakCourses2019Test.csv";
        inputGonghakCoursesCsv(csvFilePath);
    }

    private void inputCoursesCsv(String csvFilePath) throws IOException {
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {

            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                try {
                    CoursesDomain course = mapToCoursesDomain(data);
                    coursesDao.save(course);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private CoursesDomain mapToCoursesDomain(String[] data) {
        return CoursesDomain.builder()
            .courseId(Long.valueOf(data[0]))
            .name(data[2])
            .credit(Integer.parseInt(data[3]))
            .build();
    }

    private void inputGonghakCoursesCsv(String csvFilePath) {
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                try {
                    Optional<GonghakCoursesDomain> course = mapToGonghakCoursesDomain(data);
                    if (course.isPresent()) {
                        gonghakCorusesDao.save(course.get());
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Optional<GonghakCoursesDomain> mapToGonghakCoursesDomain(String[] data) {

        CoursesDomain courseDomain = coursesDao.findByName(data[6].replaceAll("\\s+", ""));
        if (courseDomain == null) {
            return Optional.empty();
        }

        // course_category_const
        String courseCategory = data[4];

        switch (courseCategory) {
            case "중핵필수", "교양필수", "교양선택", "교양선택I", "교양":
                courseCategory = "전문교양";
                break;
            case "전공기초교양", "학문기초교양":
                courseCategory = "BSM";
                break;
            case "전공필수", "전공선택", "전공(설계)", "전공주제", "전공기초":
                courseCategory = "전공";
                break;
            default:
                break;
        }

        GonghakCoursesDomain gonghakCourse = GonghakCoursesDomain.builder()
            .year(Integer.parseInt(data[0]))
            .majorsDomain(majorsDao.findByMajor(data[2]))
            .coursesDomain(courseDomain)
            .courseCategory(CourseCategoryConst.valueOf(courseCategory))
            .passCategory(data[5].substring(0, 2))
            .designCredit(Double.parseDouble(data[8]))
            .build();

        return Optional.of(gonghakCourse);
    }
}
