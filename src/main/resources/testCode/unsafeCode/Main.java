package testCode.unsafeCode;

/**
 * 无限占用空间（浪费系统内存）
 * 测试使用，所有错误示例都是如此
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<byte[]> bytes = new ArrayList<>();
        while (true) {
            bytes.add(new byte[10000]);
        }
    }
}