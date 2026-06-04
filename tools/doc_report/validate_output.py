import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
DOC = ROOT / "Student-Attendance-System-Project-Documentation-v2.docx"


def main():
    with zipfile.ZipFile(DOC) as zf:
        print(f"TESTZIP={zf.testzip()}")
        for name in [
            "word/document.xml",
            "word/styles.xml",
            "word/settings.xml",
            "word/_rels/document.xml.rels",
            "word/footer1.xml",
            "[Content_Types].xml",
        ]:
            ET.fromstring(zf.read(name))
            print(f"PARSED={name}")


if __name__ == "__main__":
    main()
