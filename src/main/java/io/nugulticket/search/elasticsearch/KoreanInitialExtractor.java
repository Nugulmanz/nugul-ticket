package io.nugulticket.search.elasticsearch;

public class KoreanInitialExtractor {

    /**
     * 한글 문자열에서 초성 추출 메서드 ("가나다" -> "ㄱㄴㄷ" )
     * @param input 한글 문자열
     * @return 초성 문자열
     */
    public static String extractInitials(String input) {
        // StringBuilder 문자열을 동적으로 생성하거나 수정할 때 사용
        StringBuilder initials = new StringBuilder();

        // 문자열을 문자 배열로 변환한 후, 각 문자에 대해 반복
        for (char ch : input.toCharArray()) {
            if (ch >= 0xAC00 && ch <= 0xD7A3) { // 문자가 한글 음절의 유니코드 범위에 포함되는지 검사
                int unicode = ch - 0xAC00;
                int initialIndex = unicode / (21 * 28); //21은 중성의 개수, 28은 종성의 개수
                initials.append(getInitialConsonant(initialIndex)); // 해당 인덱스에 맞는 초성을 찾아서 추가
            } else {
                initials.append(ch); // 한글이 아닌 경우 그대로 추가
            }
        }
        return initials.toString();
    }

    // 초성 문자 배열
    private static char getInitialConsonant(int index) {
        final char[] initials = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        };
        return initials[index];
    }

}
