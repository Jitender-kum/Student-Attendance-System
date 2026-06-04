import xml.etree.ElementTree as ET
import zipfile

NS = {"w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main"}


def get_text(paragraph):
    return "".join(t.text or "" for t in paragraph.findall(".//w:t", NS)).strip()


def main():
    path = r"G:\Project report format-VI sem.docx"
    with zipfile.ZipFile(path) as zf:
        root = ET.fromstring(zf.read("word/document.xml"))
    body = root.find("w:body", NS)
    page = 1
    for i, child in enumerate(list(body)):
        tag = child.tag.rsplit("}", 1)[-1]
        if tag == "p":
            text = get_text(child)
            if text:
                print(f"[{i}] PAGE {page} P: {text[:160]}")
            for br in child.findall(".//w:br", NS):
                if br.get(f"{{{NS['w']}}}type") == "page":
                    print(f"  -> explicit page break after paragraph index {i}")
                    page += 1
            if child.find(".//w:lastRenderedPageBreak", NS) is not None:
                print(f"  -> rendered page break around paragraph index {i}")
                page += 1
        elif tag == "tbl":
            print(f"[{i}] PAGE {page} TABLE")
        elif tag == "sectPr":
            print(f"[{i}] PAGE {page} sectPr")


if __name__ == "__main__":
    main()
