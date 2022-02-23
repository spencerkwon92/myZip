import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Integer> test = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        System.out.println(test);
        for (int i = 0; i < test.size(); i++) {
            if (i <= 3) {
                test.remove(i);
            }
        }
        System.out.println(test);
    }
}
