import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HtmlAnalyzer {
    // Path of the example file in the root folder.
    // Default method if no url is provided.
    private static final String filePath = "example.html";

    // Split the default file into an array.
    private static String[] RootFile() {
        String[] data;
        try {
            if (!filePath.toLowerCase().endsWith(".html"))
                throw new FileException("The file is not an HTML file.");

            File file = new File(filePath);
            if (!file.exists())
                throw new FileException("The file not found in the path provided.");

            Path path = Paths.get(filePath);
            List<String> lines = Files.readAllLines(path);
            data = lines.toArray(new String[0]);

        } catch (IOException e) {
            data = new String[]{"Error reading file: " + e.getMessage()};
        } catch (FileException e) {
            data = new String[]{"File Error: " + e.getMessage()};
        }
        return data;
    }

    // Retrieve the HTML structure into an array.
    private static String[] GetHtmlFromUrl(String url) {
        String[] data;
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
                data = response.body().split("\n");
            else
                throw new IOException("request status code = " + response.statusCode());

        } catch (UnknownHostException | ConnectException | HttpTimeoutException | SocketTimeoutException e) {
            data = new String[]{"URL connection error."};
        } catch (IOException | InterruptedException e) {
            data = new String[]{"URL Error: An unexpected issue occurred - " + e.getMessage()};
        }
        return data;
    }

    private static boolean MalformedHtml(String[] lines) {
        String[] matches;
        boolean isMalformed = false;

        do {
            String tag = lines[0].replace("<", "").replace(">", "");
            String regex = "</?" + tag + ">";
            Map<Boolean, List<String>> partitioned = Arrays.stream(lines)
                    .collect(Collectors.partitioningBy(i -> i.matches(regex)));
            matches = partitioned.get(true).toArray(new String[0]);

            if (matches.length % 2 == 0) // even pairs
                lines = partitioned.get(false).toArray(new String[0]);
            else { // odd pairs == incomplete structure
                isMalformed = true;
                break;
            }
        } while (lines.length > 0);

        return isMalformed;
    }

    private static String DeepestLevelText(String[] lines) {
        String deepestText;
        String[] textToFind = Arrays.stream(lines).filter(i -> !i.startsWith("<")).toArray(String[]::new);
        int[] indexes = IntStream.range(0, lines.length).filter(i -> !lines[i].startsWith("<")).toArray();
        int[] counts = new int[textToFind.length];
        int[] match = new int[2];
        int deepest;
        boolean notFound;

        for (int i = 0; i < textToFind.length; i++) {
            notFound = true;
            String[] searchArray = lines;
            int pointer = 0, c = 0;

            do {
                if (searchArray[0].startsWith("<")) { //HTML Tag
                    String text = searchArray[0].replace("<", "").replace(">", "");
                    Pattern regex = Pattern.compile("</?" + text + ">");
                    String[] finalSearchArray = searchArray;
                    match[1] = IntStream.range(1, searchArray.length).filter(x -> regex.matcher(finalSearchArray[x]).matches()).findFirst().orElse(-1);

                    if (match[1] > indexes[i]) c++;
                    else
                        indexes[i]--;

                    Set<Integer> indexSet = Arrays.stream(match).boxed().collect(Collectors.toSet());
                    searchArray = IntStream.range(0, searchArray.length).filter(x -> !indexSet.contains(x))
                            .mapToObj(x -> finalSearchArray[x]).toArray(String[]::new);

                } else { // Text to be found.
                    if (searchArray[0].equals(textToFind[i])) // text found
                        notFound = false;
                    else // fill-up test that is not useful at the moment
                        searchArray = Arrays.copyOfRange(searchArray, 1, searchArray.length);
                }
                indexes[i]--;

            } while (notFound);
            counts[i] = c;

        }

        deepest = IntStream.range(0, counts.length)
                .reduce((a, b) -> counts[a] >= counts[b] ? a : b)
                .orElse(-1);
        deepestText = textToFind[deepest];
        return deepestText;
    }

    public static void main(String[] args) {
        String[] lines;
        String result;
        if (args.length > 0)  //custom url provided
            lines = GetHtmlFromUrl(args[0]);
        else  //using the default example file in root.
            lines = RootFile();


        if (lines.length == 1)  // Error Found
            System.out.println(lines[0]);
        else { // Success in getting the structure.
            lines = Arrays.stream(lines).map(String::trim)  // Remove empty spaces before and after the text.
                    .toArray(String[]::new);
            // verifying if the structure is not Malformed.
            boolean malformed = MalformedHtml(Arrays.stream(lines).filter(i -> i.startsWith("<")).toArray(String[]::new));

            if (malformed) // malformed HTML
                System.out.println("malformed HTML");
            else { // structure is not malformed
                result = DeepestLevelText(lines);
                System.out.println(result);
            }
        }
    }

    private static class FileException extends Throwable {
        public FileException(String s) {
        }
    }
}
