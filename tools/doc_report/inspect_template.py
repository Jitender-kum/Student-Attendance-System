import zipfile


def main():
    path = r"G:\Project report format-VI sem.docx"
    with zipfile.ZipFile(path) as zf:
        print("===NAMES===")
        for name in zf.namelist():
            print(name)
        print("===DOCUMENT===")
        print(zf.read("word/document.xml").decode("utf-8"))
        print("===STYLES===")
        print(zf.read("word/styles.xml").decode("utf-8"))


if __name__ == "__main__":
    main()
