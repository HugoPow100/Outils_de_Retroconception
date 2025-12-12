public class UnderlineDemo {
    public static void main(String[] args) {
        // Code ANSI pour souligner : \u001B[4m
        // Code ANSI pour réinitialiser : \u001B[0m
        String texteSouligne = "\u001B[4mCeci est souligné\u001B[0m";
        System.out.println(texteSouligne);
    }
}
