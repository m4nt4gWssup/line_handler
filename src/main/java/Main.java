import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) { //Проверка количества аргументов
            System.out.println("Используйте: java -jar {название проекта}.jar тестовый-файл.txt");
            return;
        }

        String filename = args[0];

        long startTime = System.currentTimeMillis(); //Засекаем время начала выполнения

        List<String> lines = Files.readAllLines(Path.of(filename), Charset.defaultCharset());

        int groupCount = getLinesInfo(lines); //Получаем количество групп

        System.out.println("Время выполнения программы: " + (System.currentTimeMillis() - startTime) + " мс");

        System.out.println("Количество групп: " + groupCount);
    }

    private static int getLinesInfo(List<String> lines) throws IOException {
        Map<String, List<Integer>> index1 = new HashMap<>(); //Индекс для хранения информации о строках

        for (int i = 0; i < lines.size(); i++) {
            String[] tokens = lines.get(i).split(";");
            for (int j = 0; j < tokens.length; j++) {
                if (!tokens[j].equals("\"\"")) { //Проверка на пустое значение
                    String key = j + tokens[j];
                    index1.computeIfAbsent(key, k -> new ArrayList<>());
                    index1.get(key).add(i);
                }
            }
        }

        Map<Integer, Group> groupedValues = new HashMap<>();

        Set<List<Integer>> grouped = index1.values().stream().filter(c -> c.size() > 1)
                .collect(Collectors.toSet());//Группировка строк

        for (List<Integer> idxes : grouped) {
            Group g = null;
            for (Integer idx : idxes) {
                if (groupedValues.containsKey(idx)) { //Проверка наличия группы для данного индекса
                    g = groupedValues.get(idx);
                    break;
                }
            }
            if (g == null)
                g = new Group(); //Создание новой группы, если не найдена существующая

            for (Integer idx : idxes) { //Добавление строк в группу
                if (!groupedValues.containsKey(idx)) {
                    g.add(lines.get(idx));
                    groupedValues.put(idx, g); //Обновление группы в сгруппированных значениях
                }
            }
        }

        int i = 1;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            for (Group g : groupedValues.values().stream().distinct()
                    .filter(g -> g.size() > 1)
                    .sorted(Comparator.comparing(Group::size).reversed())
                    .collect(Collectors.toList())) {
                writer.write(g.toString(i++));
            }
        }
        return i-1;
    }

    public static class Group {
        private final List<String> lines = new ArrayList<>();

        public int size() {
            return lines.size();
        }

        public void add(String line) {
            lines.add(line);
        }

        public String toString(int number) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Группа ").append(number).append("\n");
            for (String s : lines) {
                stringBuilder.append(s).append("\n");
            }
            stringBuilder.append("\n\r");
            return stringBuilder.toString();
        }
    }
}