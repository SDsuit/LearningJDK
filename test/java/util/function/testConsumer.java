import java.util.function.Consumer;

public class testConsumer {
    public static void main(String[] args) {
        //输出Hello
        testConsumer();
        /*输出first x : 1
        second x : 1*/
        testAndThen();
    }

    /**
     *打印字符串
     */
    public static void testConsumer(){
        Consumer<String> String = s -> System.out.println(s);
        String.accept("Hello");
    }

    /**
     * 定义3个Consumer并按顺序进行调用andThen方法，其中consumer2抛出NullPointerException。
     */
    public static void testAndThen(){
        Consumer<Integer> consumer1 = x -> System.out.println("first x : " + x);
        Consumer<Integer> consumer2 = x -> {
            System.out.println("second x : " + x);
            //在consumer2中抛出异常
            throw new NullPointerException("throw exception test");
        };
        Consumer<Integer> consumer3 = x -> System.out.println("third x : " + x);

        //最终consumer3不会被执行
        consumer1.andThen(consumer2).andThen(consumer3).accept(1);
    }
}
