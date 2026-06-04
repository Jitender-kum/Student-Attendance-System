import copy
import os
import zipfile
import xml.etree.ElementTree as ET
from pathlib import Path


W_NS = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
R_NS = "http://schemas.openxmlformats.org/officeDocument/2006/relationships"
PR_NS = "http://schemas.openxmlformats.org/package/2006/relationships"
CT_NS = "http://schemas.openxmlformats.org/package/2006/content-types"
WP_NS = "http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
A_NS = "http://schemas.openxmlformats.org/drawingml/2006/main"
PIC_NS = "http://schemas.openxmlformats.org/drawingml/2006/picture"

NS = {
    "w": W_NS,
    "r": R_NS,
    "pr": PR_NS,
    "ct": CT_NS,
    "wp": WP_NS,
    "a": A_NS,
    "pic": PIC_NS,
}

for prefix, uri in {
    "w": W_NS,
    "r": R_NS,
    "wp": WP_NS,
    "a": A_NS,
    "pic": PIC_NS,
}.items():
    ET.register_namespace(prefix, uri)


ROOT = Path(__file__).resolve().parents[2]
TEMPLATE = Path(r"G:\Project report format-VI sem.docx")
OUTPUT = ROOT / "Student-Attendance-System-Project-Documentation-v2.docx"
SCREENSHOT_DIR = Path(r"C:\Users\Lenovo\OneDrive\Pictures\Screenshots")

SCREEN_IMAGES = [
    ("Sidebar.vue navigation component", SCREENSHOT_DIR / "Screenshot (570).png"),
    ("HelloWorld.vue starter component", SCREENSHOT_DIR / "Screenshot (571).png"),
    ("MainLayout.vue application layout", SCREENSHOT_DIR / "Screenshot (572).png"),
    ("App.vue root router container", SCREENSHOT_DIR / "Screenshot (573).png"),
    ("Login.vue teacher login screen", SCREENSHOT_DIR / "Screenshot (574).png"),
    ("style.css application styling", SCREENSHOT_DIR / "Screenshot (575).png"),
    ("index.html frontend entry document", SCREENSHOT_DIR / "Screenshot (576).png"),
]


SECTIONS = [
    ("Acknowledgement", [
        "I would like to express my sincere gratitude to our respected faculty members, project guide, and the Department of Computer Applications for their continuous encouragement and valuable academic guidance during the development of the Student Attendance System. Their suggestions helped in shaping the project into a practical full-stack application that addresses day-to-day attendance management challenges faced in educational institutions.",
        "I am also thankful to my classmates, friends, and family members for their support, motivation, and constructive feedback throughout the planning, design, implementation, and validation stages of this work. Their assistance strengthened both the technical quality of the project and the discipline required to complete the documentation in an organized and professional manner.",
    ]),
    ("Index", []),
    ("Introduction", [
        "The Student Attendance System is a web-based academic management application designed to digitalize the process of maintaining student attendance records. The project combines a Vue 3 frontend with a Spring Boot backend to provide a structured interface for teacher authentication, student administration, session-based attendance marking, subject management, and attendance report generation.",
        "The system improves accuracy, traceability, and operational efficiency by replacing manual registers with authenticated workflows and database-backed records. It supports day-to-day classroom operations such as maintaining departments, courses, subjects, notes, assignments, and attendance sessions, while also enabling weekly, monthly, and semester-wise reporting with export capability.",
    ]),
    ("Objectives", [
        "To provide a secure teacher login system using JWT-based authentication.",
        "To maintain structured records for departments, courses, subjects, and students.",
        "To support session-wise attendance marking with multiple status options such as present, absent, late, leave, and half day.",
        "To generate weekly, monthly, and semester-wise attendance reports for academic monitoring.",
        "To reduce manual effort, improve data consistency, and simplify attendance review and export.",
    ]),
    ("Tools / Environment", [
        "Frontend stack: Vue 3, Vite, Pinia, Vue Router, JavaScript, and modular single-file components.",
        "Backend stack: Spring Boot 3.2.5, Java 21, Spring Web, Spring Security, Spring Data JPA, and Bean Validation.",
        "Database and persistence: MySQL with migration SQL scripts and Hibernate/JPA entity mapping.",
        "Security and integration: JWT token generation, authentication filters, teacher-scoped authorization, and REST APIs.",
        "Build and development tools: Maven Wrapper for backend builds, npm-based Vite tooling for frontend development, and GitHub workspace source management.",
    ]),
    ("Analysis Document", [
        "The core problem addressed by the project is the inefficiency and error-proneness of manual attendance registers. Paper-based attendance creates difficulty in tracking absences, validating subject-wise sessions, and generating periodical reports. The proposed system centralizes the process in a role-oriented digital platform that supports attendance capture and reporting from a single interface.",
        "Primary users are teachers and academic administrators. Functional requirements include teacher sign-up and login, student record management, department/course/subject maintenance, session creation, attendance entry updates, note and assignment management, and report exports. Non-functional requirements include secure access, maintainable modular code, database consistency, responsive UI behavior, and reliable report generation for institutional use.",
        "At the data level, the system manages entities such as Teacher, Department, Course, Subject, Student, AttendanceSession, AttendanceEntry, Assignment, and Note. Inputs flow from authenticated forms to validated REST endpoints and are persisted in MySQL, while outputs are presented through dashboards, attendance tables, and downloadable Excel reports.",
    ]),
    ("Design Document", [
        "The application follows a client-server architecture. The Vue frontend handles routing, state management, user interaction, and presentation, while the Spring Boot backend exposes REST endpoints for authentication, attendance operations, academic master data, and reports. This separation improves maintainability and allows each layer to evolve independently.",
        "The frontend is organized into layouts, stores, services, and view components. The backend is organized into controllers, DTOs, services, service implementations, repositories, security components, and model classes. This modularization supports clean responsibility boundaries and simplifies testing, debugging, and future enhancement.",
        "Database design emphasizes entity relationships and integrity constraints. Students are associated with courses and departments, subjects are mapped to academic structures, attendance sessions group attendance events by date and slot, and report services aggregate persisted records into weekly, monthly, and semester-wise summaries for decision making.",
    ]),
    ("Program Code", []),
    ("Testing & Validation", [
        "Validation was performed at unit and workflow levels using both backend configuration checks and frontend interaction scenarios. Login validation verifies required credentials and authenticated route access. Attendance validation checks session creation, individual status updates, bulk status updates, and history retrieval with date or subject filters.",
        "Report validation verifies weekly, monthly, and semester-wise summaries, along with Excel export endpoints and filtering by department, course, subject, and selected students. Data integrity is supported by service-layer validation, DTO constraints, and repository-backed persistence. Successful execution of these scenarios confirms that the system meets its major operational requirements for attendance entry and review.",
        "System quality was additionally assessed through navigation consistency, form-level error handling, protected routes, and persistence of attendance-related metadata such as session names, time slots, and remarks.",
    ]),
    ("Input and Output Screens", [
        "Representative interface views available in the current repository snapshot include the teacher login experience, attendance session dashboard, and attendance report view. The embedded interface artwork illustrates the visual direction of the application, while the following captions summarize the principal input and output interactions implemented in the system.",
        "Input Screen 1: Teacher Login accepts email and password credentials and validates required fields before granting access to the protected application routes.",
        "Input Screen 2: Attendance Sessions allows selection of date, department, course, subject, time slot, and remarks before creating or updating a session for classroom attendance entry.",
        "Output Screen 1: Attendance Report displays filtered rows, attendance percentages, student-wise summaries, and export options for weekly, monthly, and semester-wise analysis.",
    ]),
    ("Limitations of the Project", [
        "The current project depends on a locally available MySQL environment and manual database configuration, which can affect portability for first-time deployment.",
        "Automated test coverage in the repository is limited, so most validation currently relies on structured functional checks and manual end-to-end verification.",
        "The implemented authentication flow is focused on teacher users and does not yet include broader role models such as students, parents, or institutional administrators.",
        "The project emphasizes web usage and has not yet been optimized as a dedicated mobile application or offline-first academic platform.",
    ]),
    ("Future Application of the Project", [
        "The system can be extended with parent and student portals, attendance notifications, and institution-wide dashboards for academic monitoring.",
        "Future versions may integrate biometric devices, QR code attendance, timetable synchronization, and cloud deployment for broader accessibility.",
        "Advanced analytics such as absentee risk prediction, trend visualization, and department-wise performance dashboards can improve administrative decision support.",
        "Role-based workflows for administrators, faculty coordinators, and students can further strengthen usability and governance across the institution.",
    ]),
    ("Bibliography", [
        "Vue.js Documentation. Official Guide for Vue 3 Application Development. https://vuejs.org/",
        "Vite Documentation. Frontend Tooling and Build System Reference. https://vite.dev/",
        "Spring Boot Documentation. Official Reference Guide for Spring Boot 3. https://spring.io/projects/spring-boot",
        "Spring Security Documentation. Authentication and Authorization Reference. https://spring.io/projects/spring-security",
        "MySQL Documentation. Reference Manual for MySQL Database Server. https://dev.mysql.com/doc/",
        "JWT Introduction. JSON Web Token Standard, RFC 7519. https://datatracker.ietf.org/doc/html/rfc7519",
    ]),
]


CODE_SNIPPETS = [
    """package com.api;\n\nimport org.springframework.boot.SpringApplication;\nimport org.springframework.boot.autoconfigure.SpringBootApplication;\n\n@SpringBootApplication\npublic class BackendApplication {\n    public static void main(String[] args) {\n        SpringApplication.run(BackendApplication.class, args);\n    }\n}""",
    """@RestController\n@RequestMapping(\"/auth\")\npublic class AuthController {\n    private final AuthService authService;\n\n    public AuthController(AuthService authService) {\n        this.authService = authService;\n    }\n\n    @PostMapping(\"/login\")\n    public ResponseEntity<AuthDtos.AuthResponse> login(\n            @Valid @RequestBody AuthDtos.LoginRequest request) {\n        return ResponseEntity.ok(authService.login(request));\n    }\n}""",
    """@RestController\n@RequestMapping(\"/attendance\")\npublic class AttendanceController {\n    @PostMapping(\"/sessions\")\n    public ResponseEntity<AttendanceSessionDtos.AttendanceSessionResponse> createSession(\n            @Valid @RequestBody AttendanceSessionDtos.AttendanceSessionRequest request) {\n        return ResponseEntity.ok(attendanceSessionService.createSession(request));\n    }\n\n    @GetMapping(\"/sessions\")\n    public ResponseEntity<List<AttendanceSessionDtos.AttendanceSessionResponse>> listSessions(\n            @RequestParam(required = false) LocalDate date) {\n        return ResponseEntity.ok(attendanceSessionService.listSessionsByDate(date, null, null, null, null));\n    }\n}""",
    """public record StudentRequest(\n    @NotBlank(message = \"Full name is required\")\n    @Size(max = 120, message = \"Full name must be at most 120 characters\")\n    String name,\n    @NotBlank(message = \"Roll number is required\")\n    @Size(max = 50, message = \"Roll number must be at most 50 characters\")\n    String rollNumber,\n    @NotBlank(message = \"Email is required\")\n    @Email(message = \"Email must be valid\")\n    String email\n) {}""",
    """import { createRouter, createWebHistory } from 'vue-router'\nimport { useAuthStore } from '../stores/auth'\n\nconst router = createRouter({\n  history: createWebHistory(import.meta.env.BASE_URL),\n  routes,\n})\n\nrouter.beforeEach((to, from, next) => {\n  const authStore = useAuthStore()\n  if (to.meta.requiresAuth && !authStore.isAuthenticated) {\n    next('/login')\n    return\n  }\n  next()\n})""",
    """<template>\n  <div class=\"login-page\">\n    <div class=\"form-panel\">\n      <div class=\"form-card\">\n        <div class=\"form-header\">\n          <h2>Teacher Login</h2>\n          <p>Sign in to your account to continue</p>\n        </div>\n      </div>\n    </div>\n  </div>\n</template>""",
]


def qn(ns, tag):
    return f"{{{ns}}}{tag}"


def w(tag):
    return qn(W_NS, tag)


def r(tag):
    return qn(R_NS, tag)


def clone(elem):
    return copy.deepcopy(elem)


def para_text(text, style=None, bold=False, align="both", size=24, font="Times New Roman", before=0, after=120, line=360):
    p = ET.Element(w("p"))
    pPr = ET.SubElement(p, w("pPr"))
    if style:
        pStyle = ET.SubElement(pPr, w("pStyle"))
        pStyle.set(w("val"), style)
    jc = ET.SubElement(pPr, w("jc"))
    jc.set(w("val"), align)
    spacing = ET.SubElement(pPr, w("spacing"))
    spacing.set(w("before"), str(before))
    spacing.set(w("after"), str(after))
    spacing.set(w("line"), str(line))
    spacing.set(w("lineRule"), "auto")

    run = ET.SubElement(p, w("r"))
    rPr = ET.SubElement(run, w("rPr"))
    rFonts = ET.SubElement(rPr, w("rFonts"))
    for attr in ("ascii", "hAnsi", "cs", "eastAsia"):
        rFonts.set(w(attr), font)
    if bold:
        ET.SubElement(rPr, w("b"))
        ET.SubElement(rPr, w("bCs"))
    sz = ET.SubElement(rPr, w("sz"))
    sz.set(w("val"), str(size))
    szCs = ET.SubElement(rPr, w("szCs"))
    szCs.set(w("val"), str(size))
    t = ET.SubElement(run, w("t"))
    if text.startswith(" ") or text.endswith(" "):
        t.set("{http://www.w3.org/XML/1998/namespace}space", "preserve")
    t.text = text
    return p


def heading(text):
    p = para_text(text, style="Heading1", bold=True, align="center", size=28, before=0, after=180, line=360)
    return p


def page_break():
    p = ET.Element(w("p"))
    r_el = ET.SubElement(p, w("r"))
    br = ET.SubElement(r_el, w("br"))
    br.set(w("type"), "page")
    return p


def toc_paragraph():
    p = ET.Element(w("p"))
    pPr = ET.SubElement(p, w("pPr"))
    jc = ET.SubElement(pPr, w("jc"))
    jc.set(w("val"), "left")
    fld = ET.SubElement(p, w("fldSimple"))
    fld.set(w("instr"), 'TOC \\o "1-1" \\h \\z \\u')
    run = ET.SubElement(fld, w("r"))
    rPr = ET.SubElement(run, w("rPr"))
    rFonts = ET.SubElement(rPr, w("rFonts"))
    for attr in ("ascii", "hAnsi", "cs", "eastAsia"):
        rFonts.set(w(attr), "Times New Roman")
    sz = ET.SubElement(rPr, w("sz"))
    sz.set(w("val"), "24")
    szCs = ET.SubElement(rPr, w("szCs"))
    szCs.set(w("val"), "24")
    t = ET.SubElement(run, w("t"))
    t.text = "Right-click and update field in Microsoft Word to refresh the table of contents."
    return p


def code_paragraph(code):
    p = ET.Element(w("p"))
    pPr = ET.SubElement(p, w("pPr"))
    spacing = ET.SubElement(pPr, w("spacing"))
    spacing.set(w("before"), "0")
    spacing.set(w("after"), "40")
    spacing.set(w("line"), "240")
    spacing.set(w("lineRule"), "auto")

    for index, line_text in enumerate(code.splitlines()):
        run = ET.SubElement(p, w("r"))
        rPr = ET.SubElement(run, w("rPr"))
        rFonts = ET.SubElement(rPr, w("rFonts"))
        for attr in ("ascii", "hAnsi", "cs", "eastAsia"):
            rFonts.set(w(attr), "Courier New")
        sz = ET.SubElement(rPr, w("sz"))
        sz.set(w("val"), "18")
        szCs = ET.SubElement(rPr, w("szCs"))
        szCs.set(w("val"), "18")
        t = ET.SubElement(run, w("t"))
        if line_text.startswith(" ") or line_text.endswith(" "):
            t.set("{http://www.w3.org/XML/1998/namespace}space", "preserve")
        t.text = line_text
        if index != len(code.splitlines()) - 1:
            ET.SubElement(run, w("br"))
    return p


def get_png_size(path):
    with open(path, "rb") as fh:
        data = fh.read(24)
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        raise ValueError("Unsupported image format")
    width = int.from_bytes(data[16:20], "big")
    height = int.from_bytes(data[20:24], "big")
    return width, height


def image_paragraph(rel_id, path, max_width_inches=5.8):
    width_px, height_px = get_png_size(path)
    width_emu = int(max_width_inches * 914400)
    height_emu = int(width_emu * height_px / width_px)

    p = ET.Element(w("p"))
    pPr = ET.SubElement(p, w("pPr"))
    jc = ET.SubElement(pPr, w("jc"))
    jc.set(w("val"), "center")

    run = ET.SubElement(p, w("r"))
    drawing = ET.SubElement(run, w("drawing"))
    inline = ET.SubElement(drawing, qn(WP_NS, "inline"))
    inline.set("distT", "0")
    inline.set("distB", "0")
    inline.set("distL", "0")
    inline.set("distR", "0")
    extent = ET.SubElement(inline, qn(WP_NS, "extent"))
    extent.set("cx", str(width_emu))
    extent.set("cy", str(height_emu))
    effect = ET.SubElement(inline, qn(WP_NS, "effectExtent"))
    for attr in ("l", "t", "r", "b"):
        effect.set(attr, "0")
    docPr = ET.SubElement(inline, qn(WP_NS, "docPr"))
    docPr.set("id", "2")
    docPr.set("name", os.path.basename(path))
    graphic = ET.SubElement(inline, qn(A_NS, "graphic"))
    graphic_data = ET.SubElement(graphic, qn(A_NS, "graphicData"))
    graphic_data.set("uri", "http://schemas.openxmlformats.org/drawingml/2006/picture")
    pic = ET.SubElement(graphic_data, qn(PIC_NS, "pic"))
    nv_pic_pr = ET.SubElement(pic, qn(PIC_NS, "nvPicPr"))
    c_nv_pr = ET.SubElement(nv_pic_pr, qn(PIC_NS, "cNvPr"))
    c_nv_pr.set("id", "0")
    c_nv_pr.set("name", os.path.basename(path))
    c_nv_pic_pr = ET.SubElement(nv_pic_pr, qn(PIC_NS, "cNvPicPr"))
    c_nv_pic_pr.set("preferRelativeResize", "0")
    blip_fill = ET.SubElement(pic, qn(PIC_NS, "blipFill"))
    blip = ET.SubElement(blip_fill, qn(A_NS, "blip"))
    blip.set(r("embed"), rel_id)
    src_rect = ET.SubElement(blip_fill, qn(A_NS, "srcRect"))
    for attr in ("l", "t", "r", "b"):
        src_rect.set(attr, "0")
    stretch = ET.SubElement(blip_fill, qn(A_NS, "stretch"))
    ET.SubElement(stretch, qn(A_NS, "fillRect"))
    sp_pr = ET.SubElement(pic, qn(PIC_NS, "spPr"))
    xfrm = ET.SubElement(sp_pr, qn(A_NS, "xfrm"))
    off = ET.SubElement(xfrm, qn(A_NS, "off"))
    off.set("x", "0")
    off.set("y", "0")
    ext = ET.SubElement(xfrm, qn(A_NS, "ext"))
    ext.set("cx", str(width_emu))
    ext.set("cy", str(height_emu))
    prst = ET.SubElement(sp_pr, qn(A_NS, "prstGeom"))
    prst.set("prst", "rect")
    ET.SubElement(sp_pr, qn(A_NS, "ln"))
    return p


def image_heading_paragraph(text):
    return para_text(text, bold=True, align="center", size=22, before=80, after=40)


def footer_xml():
    ftr = ET.Element(w("ftr"))
    p = ET.SubElement(ftr, w("p"))
    pPr = ET.SubElement(p, w("pPr"))
    jc = ET.SubElement(pPr, w("jc"))
    jc.set(w("val"), "center")

    def footer_run():
        run = ET.SubElement(p, w("r"))
        rPr = ET.SubElement(run, w("rPr"))
        rFonts = ET.SubElement(rPr, w("rFonts"))
        for attr in ("ascii", "hAnsi", "cs", "eastAsia"):
            rFonts.set(w(attr), "Times New Roman")
        sz = ET.SubElement(rPr, w("sz"))
        sz.set(w("val"), "24")
        szCs = ET.SubElement(rPr, w("szCs"))
        szCs.set(w("val"), "24")
        return run

    begin = footer_run()
    ET.SubElement(begin, w("fldChar")).set(w("fldCharType"), "begin")
    instr = footer_run()
    instr_text = ET.SubElement(instr, w("instrText"))
    instr_text.set("{http://www.w3.org/XML/1998/namespace}space", "preserve")
    instr_text.text = " PAGE "
    separate = footer_run()
    ET.SubElement(separate, w("fldChar")).set(w("fldCharType"), "separate")
    value = footer_run()
    ET.SubElement(value, w("t")).text = "1"
    end = footer_run()
    ET.SubElement(end, w("fldChar")).set(w("fldCharType"), "end")
    return ET.tostring(ftr, encoding="utf-8", xml_declaration=True)


def build_document_xml(source_document_bytes):
    source_root = ET.fromstring(source_document_bytes)
    source_body = source_root.find("w:body", NS)
    source_children = list(source_body)

    new_root = ET.Element(w("document"))
    new_body = ET.SubElement(new_root, w("body"))

    # Title page from template
    for idx in range(61, 86):
        new_body.append(clone(source_children[idx]))

    # Certificate page from template
    new_body.append(page_break())
    for idx in range(37, 61):
        new_body.append(clone(source_children[idx]))

    for title, paragraphs in SECTIONS:
        new_body.append(page_break())
        new_body.append(heading(title))
        if title == "Index":
            new_body.append(toc_paragraph())
        elif title == "Program Code":
            new_body.append(para_text("Representative code excerpts from the backend and frontend are provided below in text form to demonstrate the implementation of application startup, authentication, attendance management, validation rules, protected routing, and the teacher login interface.", before=0, after=80))
            code_titles = [
                "BackendApplication.java",
                "AuthController.java",
                "AttendanceController.java",
                "StudentDtos.java",
                "router/index.js",
                "Login.vue",
            ]
            for code_title, snippet in zip(code_titles, CODE_SNIPPETS):
                new_body.append(image_heading_paragraph(code_title))
                new_body.append(code_paragraph(snippet))
        else:
            for paragraph in paragraphs:
                new_body.append(para_text(paragraph))
            if title == "Input and Output Screens":
                for idx, (caption, path) in enumerate(SCREEN_IMAGES, start=1):
                    if path.exists():
                        rel_id = f"rIdScreen{idx}"
                        new_body.append(image_heading_paragraph(caption))
                        new_body.append(image_paragraph(rel_id, str(path), max_width_inches=6.2))

    sectPr = ET.Element(w("sectPr"))
    footer_ref = ET.SubElement(sectPr, w("footerReference"))
    footer_ref.set(w("type"), "default")
    footer_ref.set(r("id"), "rIdFooter1")
    pgSz = ET.SubElement(sectPr, w("pgSz"))
    pgSz.set(w("w"), "11906")
    pgSz.set(w("h"), "16838")
    pgMar = ET.SubElement(sectPr, w("pgMar"))
    pgMar.set(w("top"), "851")
    pgMar.set(w("right"), "851")
    pgMar.set(w("bottom"), "851")
    pgMar.set(w("left"), "851")
    pgMar.set(w("header"), "720")
    pgMar.set(w("footer"), "720")
    pgMar.set(w("gutter"), "0")
    ET.SubElement(sectPr, w("pgNumType")).set(w("start"), "1")
    new_body.append(sectPr)
    return ET.tostring(new_root, encoding="utf-8", xml_declaration=True)


def patch_styles(source_styles_bytes):
    root = ET.fromstring(source_styles_bytes)
    for style in root.findall("w:style", NS):
        style_id = style.get(w("styleId"))
        if style_id == "Normal":
            rPr = style.find("w:rPr", NS)
            if rPr is None:
                rPr = ET.SubElement(style, w("rPr"))
            rFonts = rPr.find("w:rFonts", NS)
            if rFonts is None:
                rFonts = ET.SubElement(rPr, w("rFonts"))
            for attr in ("ascii", "hAnsi", "cs", "eastAsia"):
                rFonts.set(w(attr), "Times New Roman")
            for tag in ("sz", "szCs"):
                elem = rPr.find(f"w:{tag}", NS)
                if elem is None:
                    elem = ET.SubElement(rPr, w(tag))
                elem.set(w("val"), "24")
            pPr = style.find("w:pPr", NS)
            if pPr is None:
                pPr = ET.SubElement(style, w("pPr"))
            spacing = pPr.find("w:spacing", NS)
            if spacing is None:
                spacing = ET.SubElement(pPr, w("spacing"))
            spacing.set(w("line"), "360")
            spacing.set(w("lineRule"), "auto")
            spacing.set(w("after"), "120")
        elif style_id == "Heading1":
            pPr = style.find("w:pPr", NS)
            if pPr is None:
                pPr = ET.SubElement(style, w("pPr"))
            jc = pPr.find("w:jc", NS)
            if jc is None:
                jc = ET.SubElement(pPr, w("jc"))
            jc.set(w("val"), "center")
            spacing = pPr.find("w:spacing", NS)
            if spacing is None:
                spacing = ET.SubElement(pPr, w("spacing"))
            spacing.set(w("before"), "0")
            spacing.set(w("after"), "180")
            spacing.set(w("line"), "360")
            spacing.set(w("lineRule"), "auto")
            rPr = style.find("w:rPr", NS)
            if rPr is None:
                rPr = ET.SubElement(style, w("rPr"))
            rFonts = rPr.find("w:rFonts", NS)
            if rFonts is None:
                rFonts = ET.SubElement(rPr, w("rFonts"))
            for attr in ("ascii", "hAnsi", "cs", "eastAsia"):
                rFonts.set(w(attr), "Times New Roman")
            for tag in ("sz", "szCs"):
                elem = rPr.find(f"w:{tag}", NS)
                if elem is None:
                    elem = ET.SubElement(rPr, w(tag))
                elem.set(w("val"), "28")
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def patch_settings(source_settings_bytes):
    root = ET.fromstring(source_settings_bytes)
    update = root.find("w:updateFields", NS)
    if update is None:
        update = ET.SubElement(root, w("updateFields"))
    update.set(w("val"), "true")
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def build_document_rels():
    rels = ET.Element(qn(PR_NS, "Relationships"))
    rel_footer = ET.SubElement(rels, qn(PR_NS, "Relationship"))
    rel_footer.set("Id", "rIdFooter1")
    rel_footer.set("Type", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/footer")
    rel_footer.set("Target", "footer1.xml")

    rel_logo = ET.SubElement(rels, qn(PR_NS, "Relationship"))
    rel_logo.set("Id", "rId6")
    rel_logo.set("Type", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image")
    rel_logo.set("Target", "media/image1.png")

    for idx, (_, path) in enumerate(SCREEN_IMAGES, start=1):
        if path.exists():
            rel_img = ET.SubElement(rels, qn(PR_NS, "Relationship"))
            rel_img.set("Id", f"rIdScreen{idx}")
            rel_img.set("Type", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image")
            rel_img.set("Target", f"media/screen_{idx}.png")

    return ET.tostring(rels, encoding="utf-8", xml_declaration=True)


def patch_content_types(source_bytes):
    root = ET.fromstring(source_bytes)
    found_footer = False
    for override in root.findall("ct:Override", NS):
        if override.get("PartName") == "/word/footer1.xml":
            found_footer = True
    if not found_footer:
        footer = ET.SubElement(root, qn(CT_NS, "Override"))
        footer.set("PartName", "/word/footer1.xml")
        footer.set("ContentType", "application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml")
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def main():
    with zipfile.ZipFile(TEMPLATE, "r") as src:
        files = {name: src.read(name) for name in src.namelist()}

    files["word/document.xml"] = build_document_xml(files["word/document.xml"])
    files["word/styles.xml"] = patch_styles(files["word/styles.xml"])
    files["word/settings.xml"] = patch_settings(files["word/settings.xml"])
    files["word/_rels/document.xml.rels"] = build_document_rels()
    files["[Content_Types].xml"] = patch_content_types(files["[Content_Types].xml"])
    files["word/footer1.xml"] = footer_xml()

    for idx, (_, path) in enumerate(SCREEN_IMAGES, start=1):
        if path.exists():
            files[f"word/media/screen_{idx}.png"] = path.read_bytes()

    with zipfile.ZipFile(OUTPUT, "w", zipfile.ZIP_DEFLATED) as out:
        for name, data in files.items():
            out.writestr(name, data)

    print(OUTPUT)


if __name__ == "__main__":
    main()
