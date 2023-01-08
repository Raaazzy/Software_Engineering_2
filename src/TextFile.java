import java.io.File;
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
        dependencies = new ArrayList<TextFile>();
        hasBeenVisited = false;
    }

    // Возвращает информацию о посещении файла
    public boolean getHasBeenVisited() {
        return hasBeenVisited;
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
}
