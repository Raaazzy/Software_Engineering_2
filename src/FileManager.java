import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FileManager {

    // Хранит корневой файл
    private final File rootPath;

    // Хранит список всех файлов в корневой папке
    private final List<TextFile> files;

    // Хранит количество символов в названии корневого файла
    private final int rootPathNameLength;

    // Хранит список файлов, отсортированный с помощью топологической сортировки
    private final List<TextFile> sortedFiles;

    // Мапа с пронумерованными файлами, используемая для топологической сортировки
    private final Map<TextFile, Integer> numberedSortFiles;

    public FileManager(String rootPathName) {
        rootPath = new File(rootPathName);
        files = new ArrayList<>();
        rootPathNameLength = rootPathName.length() + 1;
        sortedFiles = new ArrayList<>();
        numberedSortFiles = new HashMap<>();
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

    // Вызываем функцию для поиска зависимостей у всех найденных файлов
    public void findFilesDependencies() {
        for (TextFile file : files) {
            file.findDependencies(this);
        }
    }

    // Показывает: существует ли файл в корневом
    boolean exist(String fileName) {
        for (TextFile file : files) {
            if (Objects.equals(file.getName(), fileName)) {
                return true;
            }
        }
        return false;
    }

    // Ищет данный файл среди представленных в корневом файле
    // Если такого файла не существует, то возвращает null
    TextFile find(String fileName) {
        for (TextFile file : files) {
            if (Objects.equals(file.getName(), fileName)) {
                return file;
            }
        }
        return null;
    }

    // Проверяет файлы на наличие циклических зависимостей
    boolean anyCycles() {
        // Флаг, показывающий наличие циклической зависимости
        boolean check = false;
        int serialNumber = 0;
        // Присваиваем всем файлом порядковый номер
        for (TextFile file : sortedFiles) {
            numberedSortFiles.put(file, serialNumber++);
        }
        // Ищем циклический зависимости и сразу же их выводим
        for (TextFile mainFile : numberedSortFiles.keySet()) {
            for (TextFile dependenceFile : mainFile.getDependencies()) {
                // Если файл с меньшим порядковым номером имеет зависимость на больший порядковый номер,
                // то это точно циклическая зависимость
                if (numberedSortFiles.get(mainFile) < numberedSortFiles.get(dependenceFile) || Objects.equals(numberedSortFiles.get(mainFile), numberedSortFiles.get(dependenceFile))) {
                    if (!check) {
                        System.out.println("Обнаруженные циклические зависимости:");
                        check = true;
                    }
                    // Убираем пометки посещенности у всех файлов
                    cancelAllVisits();
                    // Выводим найденную циклическую зависимость
                    printAllFilesInRange(dependenceFile, mainFile, new ArrayList<>(List.of(dependenceFile)));
                }
            }
        }
        return check;
    }

    // Отменяет пометку посещенности у всех файлов
    private void cancelAllVisits() {
        for (TextFile file : sortedFiles) {
            file.leave();
        }
    }

    // Функция для вывода всех найденных циклических зависимостей
    private void printAllFilesInRange(TextFile fromFile, TextFile toFile, List<TextFile> localPathList) {
        // Если дошли до начального файла, то выписываем все файлы пути и прекращаем работу
        if (fromFile.equals(toFile)) {
            System.out.println(localPathList);
            return;
        }
        // Помечаем текущий файл посещенным
        fromFile.visit();
        // Пробегаемся по всем файлам, зависимым от текущего
        for (TextFile file : fromFile.getDependencies()) {
            // Если зависимый файл не был посещен, то рекурсивно вызываем функцию и ищем циклическую зависимость
            if (file.hasNotBeenVisited()) {
                localPathList.add(file);
                printAllFilesInRange(file, toFile, localPathList);
                // Удаляем этот файл из листа, потому что уже вывели его в консоль
                localPathList.remove(file);
            }
        }
        // Убираем пометку посещения файла
        fromFile.leave();
    }

    // Функция для вызова топологической сортировки для всех файлов
    public void sortFiles() {
        // пробегаемся по всем файлам и вызываем для них сортировку
        for (TextFile file : files) {
            if (file.hasNotBeenVisited()) {
                organizeFiles(file);
            }
        }
        // Проверяем, есть ли циклические зависимости
        if (!anyCycles()) {
            // Если нет, то выводит отсортированные файлы и их конкатенацию
            for (TextFile file : sortedFiles) {
                System.out.println(file);
            }
            concatenateFiles();
        }
    }

    // Топологическая сортировка файлов
    private void organizeFiles(TextFile file) {
        // Отметим файл посещенным
        file.visit();
        for (TextFile dependenceFile : file.getDependencies()) {
            // Посетим все зависимые файлы
            if (dependenceFile.hasNotBeenVisited()) {
                organizeFiles(dependenceFile);
            }
        }
        // Закидываем файл на нужное место в списке
        sortedFiles.add(file);
    }

    // Конкатенирует все отсортированные файлы
    public void concatenateFiles() {
        File concatenateFile = new File("final.txt");
        try (FileWriter writer = new FileWriter(concatenateFile, false)) {
            writer.write("");
        } catch (FileNotFoundException e) {
            System.out.println("Упс... Во время считывания файл final.txt куда-то пропал, и мы не смогли его найти. Придется начать все сначала :(");
            System. exit(0);
        } catch (IOException e) {
            System.out.println("Упс... С файлом final.txt что-то не так. Придется начать все сначала :(");
            System. exit(0);
        }
        for (TextFile file : sortedFiles) {
            file.writeTextFromFile(concatenateFile);
        }
    }
}
