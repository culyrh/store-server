package com.bookstore.api.common.util;

public class StringUtil {

    /**
     * 문자열이 null이거나 빈 문자열인지 확인
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 문자열이 null이 아니고 비어있지 않은지 확인
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 이메일 형식 검증 (간단한 정규식)
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * 전화번호 형식 검증 (한국 전화번호)
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return false;
        }
        String phoneRegex = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$";
        return phoneNumber.matches(phoneRegex);
    }

    /**
     * ISBN 형식 검증 (ISBN-10 또는 ISBN-13)
     */
    public static boolean isValidIsbn(String isbn) {
        if (isEmpty(isbn)) {
            return false;
        }
        // 하이픈 제거
        String cleanIsbn = isbn.replaceAll("-", "");
        // ISBN-10 또는 ISBN-13
        return cleanIsbn.matches("^[0-9]{10}$") || cleanIsbn.matches("^[0-9]{13}$");
    }
}