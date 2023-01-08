import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FileManager files = new FileManager(inputRootPath());
    }

    // Функция для корректного ввода названия корневой папки
    static String inputRootPath() {
        System.out.print("Введите полный путь до корневой папки: ");
        while (true) {
            // Считываем введенную строчку
            Scanner in = new Scanner(System.in);
            String inputString = in.next();
            // В названии корневого пути заменяем все найденные сепараторы на те, что поддерживает операционная система
            Path path = Paths.get(inputString.replace("\\", File.separator)
                    .replace("/", File.separator));
            if (Files.exists(path)) {
                // Если такой файл существует, то возвращаем данной название
                return inputString;
            } else {
                // Иначе, повторяем все действия
                System.out.print("\nВы ввели некорректный путь, попробуйте еще раз: ");
            }
        }
    }
}