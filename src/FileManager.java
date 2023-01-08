import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileManager {

    // Хранит корневой файл
    private final File rootPath;

    // Хранит список всех файлов в корневой папке
    private final List<TextFile> files;

    // Хранит количество символов в названии корневого файла
    private final int rootPathNameLength;

    public FileManager(String rootPathName) {
        rootPath = new File(rootPathName);
        files = new ArrayList<TextFile>();
        rootPathNameLength = rootPathName.length() + 1;
    }

    // Возвращает корневой файл
    public File getRootPath() {
        return rootPath;
    }

    // Сканируем текущую директорию и выискиваем все файлы в данной папке и во всех зависимых
    public void scanDirectory(File currentPath) {
        // Пробегаемся по всем файлам в папке (если такие есть)
        for (File currentFile : Objects.requireNonNull(currentPath.listFiles())) {
            // Проверяем, это файл или нет, а также его расширение
            if (currentFile.isFile() && getFileExtension(currentFile).equals("txt")) {
                // Записываем имя файла без учета корневой папки и расширения
                String fileNameWithoutRoot = currentFile.getAbsoluteFile()
                        .toString()
                        .substring(rootPathNameLength, currentFile.getAbsoluteFile()
                                .toString()
                                .length() - 4);
                // Добавляем файл в список всех найденный файлов
                files.add(new TextFile(fileNameWithoutRoot, currentFile));
            } else if (currentFile.isDirectory()) {
                // Если это папка, то рекурсивно вызываем функцию для нее
                scanDirectory(currentFile);
            }
        }
    }

    // Возвращает расширение файла
    private String getFileExtension(File file) {
        String fileName = file.getName();
        // если в имени файла есть точка и она не является первым символом в названии файла
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            // то вырезаем все знаки после последней точки в названии файла
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
