import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Класс определяющий поведение текстового файла
public class TextFile {

    // Хранит имя файла
    private final String name;

    // Хранит все файлы, от которых зависит данный
    private final List<TextFile> dependencies;

    // Хранит текущий файл
    private final File file;

    // Хранит информацию о посещении файла
    private boolean hasBeenVisited;

    public TextFile(String name, File file) {
        this.name = name;
        this.file = file;
        dependencies = new ArrayList<>();
        hasBeenVisited = false;
    }

    // Возвращает информацию о посещении файла
    public boolean hasNotBeenVisited() {
        return !hasBeenVisited;
    }

    // Возвращает все файлы, от которых зависит данный
    public List<TextFile> getDependencies() {
        return dependencies;
    }

    // Возвращает имя файла
    public String getName() {
        return name;
    }

    // Меняет статус файла на "посещенный"
    public void visit() {
        hasBeenVisited = true;
    }

    // Меняет статус файла на "не посещенный"
    public void leave() {
        hasBeenVisited = false;
    }

    // Выискивает все файлы, от которых зависит данный
    public void findDependencies(FileManager fileManager) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            // Считываем файл построчно, пока не дойдем до конца
            String line = reader.readLine();
            while (line != null) {
                if (line.matches("^require ‘(.+)’$")) {
                    // Заменяем все найденные сепараторы на те, что поддерживает операционная система
                    String fileName = line.substring(9, line.length() - 1)
                            .replace("\\", File.separator)
                            .replace("/", File.separator);
                    if (fileManager.exist(fileName)) {
                        // Добавляем файл в зависимые (если такого файла нет, то добавится null)
                        TextFile file = fileManager.find(fileName);
                        if (!dependencies.contains(file)) {
                            dependencies.add(file);
                        }
                    } else {
                        System.out.println("Упс... Файла " + fileName + " не существует. Придется начать все сначала :(");
                        System.exit(0);
                    }
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Упс... Во время считывания файл " + name + " куда-то пропал, и мы не смогли его найти. Придется начать все сначала :(");
            System.exit(0);
        } catch (SecurityException e) {
            System.out.println("Упс... У нас нет доступа к файлу из=за его уровня доступности. Придется начать все сначала :(");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Упс... С файлом " + name + " что-то не так. Придется начать все сначала :(");
            System.exit(0);
        }
    }

    // Записывает текст с заданного файла в финальный
    void writeTextFromFile(File otherFile) {
        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader reader = new BufferedReader(fileReader);
            try (FileWriter writer = new FileWriter(otherFile, true)) {
                // Считываем файл построчно, пока не дойдем до конца
                String line = reader.readLine();
                while (line != null) {
                    // записываем данные построчно с заданного файла в финальный
                    writer.append(line)
                            .append("\n");
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException();
            } catch (SecurityException e) {
                throw new SecurityException();
            } catch (IOException e) {
                throw new IOException();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Упс... Во время считывания файл " + name + " или " + otherFile.getName() + " куда-то пропал, и мы не смогли его найти. Придется начать все сначала :(");
            System.exit(0);
        } catch (SecurityException e) {
            System.out.println("Упс... У нас нет доступа к файлу из=за его уровня доступности. Придется начать все сначала :(");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Упс... С файлом " + name + " или " + otherFile.getName() + " что-то не так. Придется начать все сначала :(");
            System.exit(0);
        }
    }

    // Переопределенный toString
    @Override
    public String toString() {
        return getName();
    }
}
