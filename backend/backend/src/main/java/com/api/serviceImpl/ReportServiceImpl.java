package com.api.serviceImpl;

import com.api.dto.ReportDtos;
import com.api.enums.AttendanceStatus;
import com.api.model.AttendanceEntry;
import com.api.model.Teacher;
import com.api.repository.AttendanceEntryRepository;
import com.api.security.AuthenticatedTeacherService;
import com.api.service.ReportService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ReportServiceImpl implements ReportService {

    private final AttendanceEntryRepository attendanceEntryRepository;
    private final AuthenticatedTeacherService authenticatedTeacherService;

    public ReportServiceImpl(AttendanceEntryRepository attendanceEntryRepository,
                             AuthenticatedTeacherService authenticatedTeacherService) {
        this.attendanceEntryRepository = attendanceEntryRepository;
        this.authenticatedTeacherService = authenticatedTeacherService;
    }

    @Override
    public List<ReportDtos.AttendanceReportRow> getWeeklyReport(LocalDate startDate, Long departmentId, Long courseId, Long subjectId) {
        LocalDate end = startDate.plusDays(6);
        return generateReport(startDate, end, departmentId, courseId, subjectId, null, null, null);
    }

    @Override
    public List<ReportDtos.AttendanceReportRow> getMonthlyReport(int year, int month, Long departmentId, Long courseId, Long subjectId) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
        return generateReport(start, end, departmentId, courseId, subjectId, null, null, null);
    }

    @Override
    public List<ReportDtos.AttendanceReportRow> getSemesterReport(Integer year, Integer semester, Long departmentId, Long courseId, Long subjectId) {
        LocalDate now = LocalDate.now();
        return generateReport(
                LocalDate.of(now.getYear(), 1, 1),
                LocalDate.of(now.getYear(), 12, 31),
                departmentId,
                courseId,
                subjectId,
                year,
                semester,
                null
        );
    }

    @Override
    public byte[] exportWeeklyReport(LocalDate startDate, Long departmentId, Long courseId, Long subjectId, List<Long> studentIds) {
        LocalDate end = startDate.plusDays(6);
        return exportReport(
                generateReport(startDate, end, departmentId, courseId, subjectId, null, null, studentIds),
                weeklyMetadata(startDate, end)
        );
    }

    @Override
    public byte[] exportMonthlyReport(int year, int month, Long departmentId, Long courseId, Long subjectId, List<Long> studentIds) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
        return exportReport(
                generateReport(start, end, departmentId, courseId, subjectId, null, null, studentIds),
                monthlyMetadata(year, month, start, end)
        );
    }

    @Override
    public byte[] exportSemesterReport(Integer year, Integer semester, Long departmentId, Long courseId, Long subjectId, List<Long> studentIds) {
        LocalDate now = LocalDate.now();
        LocalDate start = LocalDate.of(now.getYear(), 1, 1);
        LocalDate end = LocalDate.of(now.getYear(), 12, 31);
        return exportReport(generateReport(
                start,
                end,
                departmentId,
                courseId,
                subjectId,
                year,
                semester,
                studentIds
        ), semesterMetadata(year, semester, start, end));
    }

    private List<ReportDtos.AttendanceReportRow> generateReport(LocalDate startDate,
                                                                LocalDate endDate,
                                                                Long departmentId,
                                                                Long courseId,
                                                                Long subjectId,
                                                                Integer year,
                                                                Integer semester,
                                                                List<Long> studentIds) {
        Teacher teacher = authenticatedTeacherService.getCurrentTeacher();
        Set<Long> selectedStudentIds = toStudentIdSet(studentIds);

        List<AttendanceEntry> attendanceRecords = attendanceEntryRepository.findForReport(
                teacher.getId(),
                startDate,
                endDate,
                departmentId,
                courseId,
                subjectId,
                year,
                semester
        );
        if (!selectedStudentIds.isEmpty()) {
            attendanceRecords = attendanceRecords.stream()
                    .filter(record -> selectedStudentIds.contains(record.getStudent().getId()))
                    .toList();
        }

        return attendanceRecords.stream()
                .map(this::buildRow)
                .toList();
    }

    private Set<Long> toStudentIdSet(List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(studentIds);
    }

    private byte[] exportReport(List<ReportDtos.AttendanceReportRow> rows, ExportMetadata metadata) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            writeZipEntry(zip, "[Content_Types].xml", contentTypesXml());
            writeZipEntry(zip, "_rels/.rels", rootRelsXml());
            writeZipEntry(zip, "xl/workbook.xml", workbookXml());
            writeZipEntry(zip, "xl/_rels/workbook.xml.rels", workbookRelsXml());
            writeZipEntry(zip, "xl/styles.xml", stylesXml());
            writeZipEntry(zip, "xl/worksheets/sheet1.xml", worksheetXml(rows, metadata));
            zip.finish();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate attendance report export", e);
        }
    }

    private void writeZipEntry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String contentTypesXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                  <Default Extension="xml" ContentType="application/xml"/>
                  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
                </Types>
                """;
    }

    private String rootRelsXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                </Relationships>
                """;
    }

    private String workbookXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
                          xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                  <sheets>
                    <sheet name="Attendance Report" sheetId="1" r:id="rId1"/>
                  </sheets>
                </workbook>
                """;
    }

    private String workbookRelsXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
                </Relationships>
                """;
    }

    private String stylesXml() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                  <numFmts count="1">
                    <numFmt numFmtId="164" formatCode="0.00%"/>
                  </numFmts>
                  <fonts count="2">
                    <font><sz val="11"/><name val="Calibri"/><family val="2"/></font>
                    <font><b/><sz val="11"/><name val="Calibri"/><family val="2"/></font>
                  </fonts>
                  <fills count="2">
                    <fill><patternFill patternType="none"/></fill>
                    <fill><patternFill patternType="gray125"/></fill>
                  </fills>
                  <borders count="1">
                    <border><left/><right/><top/><bottom/><diagonal/></border>
                  </borders>
                  <cellStyleXfs count="1">
                    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
                  </cellStyleXfs>
                  <cellXfs count="3">
                    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
                    <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/>
                    <xf numFmtId="164" fontId="0" fillId="0" borderId="0" xfId="0" applyNumberFormat="1"/>
                  </cellXfs>
                  <cellStyles count="1">
                    <cellStyle name="Normal" xfId="0" builtinId="0"/>
                  </cellStyles>
                </styleSheet>
                """;
    }

    private String worksheetXml(List<ReportDtos.AttendanceReportRow> rows, ExportMetadata metadata) {
        String[] headers = {
                "Student Name",
                "Roll Number",
                "Department",
                "Course",
                "Subject",
                "Period",
                "Lecture Start Time",
                "Lecture End Time",
                "Session Date",
                "Session Name",
                "Year",
                "Semester",
                "Present Count",
                "Absent Count",
                "Late Count",
                "Leave Count",
                "Half Day Count",
                "Total Working Days",
                "Attendance Percentage"
        };

        StringBuilder xml = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                  <sheetData>
                """);

        int rowIndex = 1;
        for (MetadataRow metadataRow : metadata.rows()) {
            xml.append("<row r=\"").append(rowIndex).append("\">")
                    .append(inlineStringCell(cellRef(0, rowIndex), metadataRow.label(), 1))
                    .append(inlineStringCell(cellRef(1, rowIndex), metadataRow.value(), 0))
                    .append("</row>");
            rowIndex++;
        }

        rowIndex++;

        xml.append("<row r=\"").append(rowIndex).append("\">");
        for (int index = 0; index < headers.length; index++) {
            xml.append(inlineStringCell(cellRef(index, rowIndex), headers[index], 1));
        }
        xml.append("</row>");

        rowIndex++;
        for (ReportDtos.AttendanceReportRow row : rows) {
            xml.append("<row r=\"").append(rowIndex).append("\">")
                    .append(inlineStringCell(cellRef(0, rowIndex), row.studentName(), 0))
                    .append(inlineStringCell(cellRef(1, rowIndex), row.rollNumber(), 0))
                    .append(inlineStringCell(cellRef(2, rowIndex), row.departmentName(), 0))
                    .append(inlineStringCell(cellRef(3, rowIndex), row.courseName(), 0))
                    .append(inlineStringCell(cellRef(4, rowIndex), row.subjectName(), 0))
                    .append(inlineStringCell(cellRef(5, rowIndex), row.periodLabel(), 0))
                    .append(inlineStringCell(cellRef(6, rowIndex), row.startTime(), 0))
                    .append(inlineStringCell(cellRef(7, rowIndex), row.endTime(), 0))
                    .append(inlineStringCell(cellRef(8, rowIndex), row.sessionDate(), 0))
                    .append(inlineStringCell(cellRef(9, rowIndex), row.sessionName(), 0))
                    .append(integerCell(cellRef(10, rowIndex), row.year()))
                    .append(integerCell(cellRef(11, rowIndex), row.semester()))
                    .append(numberCell(cellRef(12, rowIndex), row.presentCount(), 0))
                    .append(numberCell(cellRef(13, rowIndex), row.absentCount(), 0))
                    .append(numberCell(cellRef(14, rowIndex), row.lateCount(), 0))
                    .append(numberCell(cellRef(15, rowIndex), row.leaveCount(), 0))
                    .append(numberCell(cellRef(16, rowIndex), row.halfDayCount(), 0))
                    .append(numberCell(cellRef(17, rowIndex), row.totalWorkingDays(), 0))
                    .append(numberCell(cellRef(18, rowIndex), row.attendancePercentage() / 100.0, 2))
                    .append("</row>");
            rowIndex++;
        }

        xml.append("""
                  </sheetData>
                </worksheet>
                """);
        return xml.toString();
    }

    private String inlineStringCell(String reference, String value, int styleIndex) {
        return "<c r=\"" + reference + "\" t=\"inlineStr\" s=\"" + styleIndex + "\"><is><t>"
                + escapeXml(nullSafe(value))
                + "</t></is></c>";
    }

    private String integerCell(String reference, Integer value) {
        return value == null ? emptyCell(reference) : numberCell(reference, value, 0);
    }

    private String numberCell(String reference, Number value, int styleIndex) {
        return "<c r=\"" + reference + "\" s=\"" + styleIndex + "\"><v>" + value + "</v></c>";
    }

    private String emptyCell(String reference) {
        return "<c r=\"" + reference + "\"/>";
    }

    private String cellRef(int columnIndex, int rowIndex) {
        int current = columnIndex;
        StringBuilder column = new StringBuilder();
        do {
            column.insert(0, (char) ('A' + (current % 26)));
            current = (current / 26) - 1;
        } while (current >= 0);
        return column + Integer.toString(rowIndex);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String escapeXml(String value) {
        return nullSafe(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private ExportMetadata weeklyMetadata(LocalDate startDate, LocalDate endDate) {
        String period = startDate + " to " + endDate;
        return new ExportMetadata(period, List.of(
                new MetadataRow("Report Type", "Weekly"),
                new MetadataRow("Period", period),
                new MetadataRow("Start Date", startDate.toString()),
                new MetadataRow("End Date", endDate.toString())
        ));
    }

    private ExportMetadata monthlyMetadata(int year, int month, LocalDate startDate, LocalDate endDate) {
        String monthLabel = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String period = monthLabel + " " + year;
        return new ExportMetadata(period, List.of(
                new MetadataRow("Report Type", "Monthly"),
                new MetadataRow("Period", period),
                new MetadataRow("Month", monthLabel),
                new MetadataRow("Year", Integer.toString(year)),
                new MetadataRow("Start Date", startDate.toString()),
                new MetadataRow("End Date", endDate.toString())
        ));
    }

    private ExportMetadata semesterMetadata(Integer year, Integer semester, LocalDate startDate, LocalDate endDate) {
        String academicYear = year != null ? year.toString() : "All";
        String semesterLabel = semester != null ? semester.toString() : "All";
        String period = "Year " + academicYear + " / Semester " + semesterLabel;
        List<MetadataRow> rows = new ArrayList<>();
        rows.add(new MetadataRow("Report Type", "Semester"));
        rows.add(new MetadataRow("Period", period));
        rows.add(new MetadataRow("Academic Year", academicYear));
        rows.add(new MetadataRow("Semester", semesterLabel));
        rows.add(new MetadataRow("Start Date", startDate.toString()));
        rows.add(new MetadataRow("End Date", endDate.toString()));
        return new ExportMetadata(period, rows);
    }

    private record MetadataRow(String label, String value) { }

    private record ExportMetadata(String periodLabel, List<MetadataRow> rows) { }

    private ReportDtos.AttendanceReportRow buildRow(AttendanceEntry record) {
        var student = record.getStudent();
        var session = record.getAttendanceSession();
        long presentCount = record.getStatus() == AttendanceStatus.PRESENT ? 1 : 0;
        long absentCount = record.getStatus() == AttendanceStatus.ABSENT ? 1 : 0;
        long lateCount = record.getStatus() == AttendanceStatus.LATE ? 1 : 0;
        long leaveCount = record.getStatus() == AttendanceStatus.LEAVE ? 1 : 0;
        long halfDayCount = record.getStatus() == AttendanceStatus.HALF_DAY ? 1 : 0;
        double percentage = (presentCount + lateCount + halfDayCount) > 0 ? 100.0 : 0.0;
        return new ReportDtos.AttendanceReportRow(
                student.getId(),
                student.getName(),
                student.getRollNo(),
                student.getDepartmentRef() != null ? student.getDepartmentRef().getName() : student.getLegacyDepartment(),
                student.getCourseRef() != null ? student.getCourseRef().getName() : student.getLegacyCourse(),
                session.getSubject() != null ? session.getSubject().getName() : null,
                session.getPeriodLabel(),
                session.getStartTime() != null ? session.getStartTime().toString() : null,
                session.getEndTime() != null ? session.getEndTime().toString() : null,
                session.getSessionDate() != null ? session.getSessionDate().toString() : null,
                session.getSessionName(),
                student.getYear(),
                student.getSemester(),
                presentCount,
                absentCount,
                lateCount,
                leaveCount,
                halfDayCount,
                1,
                Math.round(percentage * 100.0) / 100.0
        );
    }
}
