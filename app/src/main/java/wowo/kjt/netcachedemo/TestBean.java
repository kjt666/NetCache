package wowo.kjt.netcachedemo;

import java.util.List;

/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2021/03/23
 * desc :
 * version: 1.0
 * </pre>
 */
public class TestBean {

    public String name;
    public int age;
    public float weight;
    public List<String> favs;
    public Travel travel;

    @Override
    public String toString() {
        return "TestBean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", favs=" + favs +
                ", travel=" + travel +
                '}';
    }

    static class Travel{
        public String startingPoint;
        public String endingPoint;

        @Override
        public String toString() {
            return "Travel{" +
                    "startingPoint='" + startingPoint + '\'' +
                    ", endingPoint='" + endingPoint + '\'' +
                    '}';
        }
    }
}
