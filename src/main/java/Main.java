import java.util.*;
import java.util.concurrent.*;

public class Main {
    static final Integer length = 25;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[length];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        List<Future<Long>> threads = new ArrayList<>();

        final ExecutorService threadPool = Executors.newFixedThreadPool(length);

        for (String text : texts) {
            final Future<Long> task = threadPool.submit(() -> {
                        long startTs = System.currentTimeMillis();
                        int maxSize = 0;
                        for (int i = 0; i < text.length(); i++) {
                            for (int j = 0; j < text.length(); j++) {
                                if (i >= j) {
                                    continue;
                                }
                                boolean bFound = false;
                                for (int k = i; k < j; k++) {
                                    if (text.charAt(k) == 'b') {
                                        bFound = true;
                                        break;
                                    }
                                }
                                if (!bFound && maxSize < j - i) {
                                    maxSize = j - i;
                                }
                            }
                        }
//                        System.out.println(text.substring(0, 100) + " -> " + maxSize);
                        long endTs = System.currentTimeMillis();
//                        System.out.println("Time: " + (endTs - startTs) + "ms");
                        return endTs - startTs;
                    }
            );
            threads.add(task);
        }

        List<Long> results = new ArrayList<>();

        for (Future task : threads) {
            Long time = (Long) task.get();
            System.out.println("Time: " + (time) + "ms");
            results.add(time);
        }

        Long res = results
                .stream()
                .max((a, b) -> {
                    if (a > b) return 1;
                    if (a < b) return -1;
                    return 0;
                })
                .get();

        System.out.println("Max time is " + res + "ms");

        threadPool.shutdown();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
