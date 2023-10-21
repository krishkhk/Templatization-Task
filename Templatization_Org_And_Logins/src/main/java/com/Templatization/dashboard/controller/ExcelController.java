package com.Templatization.dashboard.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;

import com.Templatization.dashboard.entity.User;
import com.Templatization.dashboard.repo.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Controller
public class ExcelController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "temp";
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                List<User> users = new ArrayList<>();

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                      
                        continue;
                    }
                    User user = new User();
                    user.setFirstName(row.getCell(0).getStringCellValue());
                    user.setLastName(row.getCell(1).getStringCellValue());
                    user.setDateOfBirth(row.getCell(2).getDateCellValue());
                    user.setCity(row.getCell(3).getStringCellValue());
                    users.add(user);
                }

               
                userRepository.saveAll(users);
            } catch (IOException e) {
                
            }
        }

        return "redirect:/";
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() throws IOException {
        List<User> users = userRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");
        
        
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("First Name");
        headerRow.createCell(1).setCellValue("Last Name");
        headerRow.createCell(2).setCellValue("Date of Birth");
        headerRow.createCell(3).setCellValue("City");

        int rowIdx = 1; 

        for (User user : users) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(user.getFirstName());
            row.createCell(1).setCellValue(user.getLastName());
            Cell dateOfBirthCell = row.createCell(2);
            dateOfBirthCell.setCellValue(user.getDateOfBirth());

            
            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            dateOfBirthCell.setCellStyle(cellStyle);

            row.createCell(3).setCellValue(user.getCity());
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        byte[] excelContent = byteArrayOutputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
    }

}

