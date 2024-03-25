package com.example.gimmegonghakauth.service;

import com.example.gimmegonghakauth.dao.CompletedCoursesDao;
import com.example.gimmegonghakauth.dao.CoursesDao;
import com.example.gimmegonghakauth.dao.UserDao;
import com.example.gimmegonghakauth.domain.CompletedCoursesDomain;
import com.example.gimmegonghakauth.domain.CoursesDomain;
import com.example.gimmegonghakauth.domain.UserDomain;
import com.example.gimmegonghakauth.exception.FileException;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompletedCoursesService {

    private final CompletedCoursesDao completedCoursesDao;
    private final CoursesDao coursesDao; // CoursesDao 변수 선언
    private final UserDao userDao;

    @Autowired
    public CompletedCoursesService(CompletedCoursesDao completedCoursesDao, CoursesDao coursesDao,
        UserDao userDao) {
        this.completedCoursesDao = completedCoursesDao;
        this.coursesDao = coursesDao; // 생성자를 통한 CoursesDao 초기화
        this.userDao = userDao;
    }

    final int FIRST_ROW = 4;

    public List<CompletedCoursesDomain> extractExcelFile(MultipartFile file, UserDetails userDetails)
        throws IOException { //엑셀 데이터 추출
        List<CompletedCoursesDomain> dataList = new ArrayList<>();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        validateExcelFile(file, extension); //업로드 파일 검증
        Long studentId = Long.parseLong(userDetails.getUsername());
        UserDomain userDomain = userDao.findByStudentId(studentId).get();
        // 로그인한 유저

        checkUserDomain(userDomain);

        Workbook workbook = creatWorkbook(file, extension);
        Sheet worksheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();

        validateExcelContent(worksheet, dataFormatter);//엑셀 내용 검증

        for (int i = FIRST_ROW; i < worksheet.getPhysicalNumberOfRows(); i++) { //데이터 추출
            Row row = worksheet.getRow(i);
            CompletedCoursesDomain data = new CompletedCoursesDomain();

            String yearAsString = dataFormatter.formatCellValue(row.getCell(1));
            Integer year = Integer.parseInt(yearAsString);  //년도

            String semesterAsString = dataFormatter.formatCellValue(row.getCell(2));
            Integer semester = Integer.parseInt(String.valueOf(semesterAsString.charAt(0))); //학기

            String courseIdAsString = dataFormatter.formatCellValue(row.getCell(3));
            Long courseId = Long.parseLong(courseIdAsString); //학수번호

            CoursesDomain coursesDomain = coursesDao.findByCourseId(
                courseId);// 학수번호를 기반으로 Courses 테이블 검색

            data = CompletedCoursesDomain.builder().userDomain(userDomain)
                .coursesDomain(coursesDomain).year(year).semester(semester).build();

            completedCoursesDao.save(data);

            dataList.add(data);
        }

        return dataList; //데이터 반환
    }

    //업로드 파일 검증
    public void validateExcelFile(MultipartFile file, String extension) throws FileException {
        if (file.isEmpty()) { // 파일이 비어있으면
            throw new FileException("파일이 비어 있습니다.");
        }
        if (!extension.equals("xlsx") && !extension.equals("xls")) { //엑셀파일이 아니면
            throw new FileException("엑셀 파일만 업로드 해주세요.");
        }
    }

    // 엑셀 내용 검증
    public void validateExcelContent(Sheet workSheet, DataFormatter dataFormatter)
        throws FileException {
        if (workSheet == null) {
            throw new FileException("엑셀파일이 비어있습니다.");
        }
        Row row = workSheet.getRow(0);
        if (row == null) { //엑셀파일이 비어있으면
            throw new FileException("엑셀파일이 비어있습니다.");
        }
        String data = dataFormatter.formatCellValue(row.getCell(0));
        if (!data.equals("기이수성적")) { //형식이 올바르지 않으면
            throw new FileException("기이수성적 엑셀파일을 업로드 해주세요.");
        }
    }

    //확장자에 맞춰서 workbook 리턴
    private Workbook creatWorkbook(MultipartFile file, String extension) throws IOException {
        Workbook workbook = null;
        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (extension.equals("xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        }
        return workbook;
    }

    public void checkUserDomain(UserDomain userDomain) {
        // CompletedCourses 테이블에서 파일을 업로드한 유저정보를 가지는 행들을 불러옴
        List<CompletedCoursesDomain> coursesList = completedCoursesDao.findByUserDomain(userDomain);
        // List가 Empty 가 아니면 (해당 유저가 파일을 업로드한 적이 있으면)
        if (!coursesList.isEmpty()) {
            // CompletedCourses 테이블에서 해당하는 행들을 삭제
            completedCoursesDao.deleteAllInBatch(coursesList);
        }
    }


}
