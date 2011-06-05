import java.util.HashSet;

public class Strings {

    static Character findFirstNonRepeated(String s) {
        if(s == null || s.length() == 0) {
            return null;
        }
        if(s.length() == 1) {
            return Character.valueOf(s.charAt(0));
        }
        HashSet<Character> chars = new HashSet<Character>();
        for (int i = 0; i < s.length(); i++) {
            Character character = Character.valueOf(s.charAt(i));
            if(!chars.add(character)) {
                return character;
            }
        }
        return null;
    }


    static String removeChars(String s, String toRemove) {
        if(s == null || s.isEmpty() || toRemove == null || toRemove.isEmpty()) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        HashSet<Character> del = new HashSet<Character>();
        for (int i = 0; i < toRemove.length(); i++) {
            del.add(Character.valueOf(toRemove.charAt(i)));
        }
        for (int i = 0; i < s.length(); i++) {
            char charAt = s.charAt(i);
            if(!del.contains(Character.valueOf(charAt))) {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    static char [] removeChars(char[] s, char[] toRemove) {
        if(s == null || s.length == 0 || toRemove == null || toRemove.length == 0) {
            return s;
        }
        HashSet<Character> del = new HashSet<Character>();
        for (int i = 0; i < toRemove.length; i++) {
            del.add(Character.valueOf(toRemove[i]));
        }
        int curr = 0;
        for (int i = 0; i < s.length; i++) {
            char charAt = s[i];
            if(!del.contains(Character.valueOf(charAt))) {
                s[curr] = charAt;
                curr ++;
            }
        }
        char [] newChars = new char[curr];
        System.arraycopy(s, 0, newChars, 0, curr);
        return newChars;
    }

    public static void main(String[] args) {
        assert findFirstNonRepeated("aaaaaa") .equals( Character.valueOf('a') ): findFirstNonRepeated("aaaaaa");
        assert findFirstNonRepeated("abcdefg") == null : findFirstNonRepeated("abcdefg");
        assert findFirstNonRepeated("abcadefg") .equals( Character.valueOf('a') ) : findFirstNonRepeated("abcadefg");

        assert "".equals(removeChars("aaaaaaaa", "a")) : removeChars("aaaaaaaa", "a");
        assert "b".equals(removeChars("aaabaaaa", "a")) : removeChars("aaabaaaa", "a");
        assert "abc".equals(removeChars("abc", "")) : removeChars("abc", "");
        assert "abc".equals(removeChars("abc", "d")) : removeChars("abc", "d");
        assert "".equals(removeChars("", "d")) : removeChars("", "d");


        assert "".equals(new String(removeChars("aaaaaaaa".toCharArray(), "a".toCharArray()))) : removeChars("aaaaaaaa", "a");
        assert "b".equals(new String(removeChars("aaabaaaa".toCharArray(), "a".toCharArray()))) : removeChars("aaabaaaa", "a");
        assert "abc".equals(new String(removeChars("abc".toCharArray(), "".toCharArray()))) : removeChars("abc", "");
        assert "abc".equals(new String(removeChars("abc".toCharArray(), "d".toCharArray()))) : removeChars("abc", "d");
        assert "".equals(new String(removeChars("".toCharArray(), "d".toCharArray()))) : removeChars("", "d");
    }
}
