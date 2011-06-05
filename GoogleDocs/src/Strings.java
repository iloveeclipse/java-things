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

    static char [] reverseWords(char[] s) {
        char [] rev = new char[s.length];
        int lastSrcPos = -1;
        int currDestPos = 0;
        for (int i = s.length - 1; i >=0; i--) {
            char c = s[i];
            if(c == ' ' || i == 0) {
                if(lastSrcPos >= 0) {
                    int end = c == ' '? i +1 :i;
                    int length = lastSrcPos - end +1;
                    System.arraycopy(s, end, rev, currDestPos, length);
                    currDestPos += length;
                    rev[currDestPos++] = ' ';
                    lastSrcPos = -1;
                }
            } else if(lastSrcPos < 0){
                lastSrcPos = i;
            }
        }
        if(currDestPos < rev.length - 1) {
            for (int i = currDestPos; i < rev.length; i++) {
                rev[i] = ' ';
            }
        }
        return rev;
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

        System.out.println(new String(reverseWords("".toCharArray())));
        System.out.println(new String(reverseWords(" ".toCharArray())));
        System.out.println(new String(reverseWords("a ".toCharArray())));
        System.out.println(new String(reverseWords(" a ".toCharArray())));
        System.out.println(new String(reverseWords(" a".toCharArray())));
        System.out.println(new String(reverseWords(" ab ".toCharArray())));
        System.out.println(new String(reverseWords(" abc def   ".toCharArray())));
        System.out.println(new String(reverseWords("'abc def   '".toCharArray())));
        System.out.println(new String(reverseWords(" a b c d e f g".toCharArray())));
    }
}
