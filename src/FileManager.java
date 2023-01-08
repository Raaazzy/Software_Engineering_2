import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    // Хранит корневой файл
    private final File rootPath;

    // Хранит список всех файлов в корневой папке
    private final List<TextFile> files;

    public FileManager(String rootPathName) {
        rootPath = new File(rootPathName);
        files = new ArrayList<TextFile>();
    }

    // Возвращает корневой файл
    public File getRootPath() {
        return rootPath;
    }
}
