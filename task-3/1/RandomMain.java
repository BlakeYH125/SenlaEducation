public class RandomMain {
    public static void main(String[] args) {
        int number = (new java.util.Random()).nextInt(100, 1000);
        int sum = number % 10 + number / 100 + number % 100 / 10;
        System.out.println("Случайное трехзначное число: " + number);
        System.out.println("Сумма его цифр: " + sum);
    }
}
