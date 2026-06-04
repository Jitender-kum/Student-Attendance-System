import importlib.util
import os


def main():
    template_path = r"G:\Project report format-VI sem.docx"
    print(f"TEMPLATE_EXISTS={os.path.exists(template_path)}")
    print(f"DOCX_INSTALLED={importlib.util.find_spec('docx') is not None}")
    print(f"WIN32COM_INSTALLED={importlib.util.find_spec('win32com') is not None}")


if __name__ == "__main__":
    main()
