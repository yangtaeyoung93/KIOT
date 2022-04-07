package com.airguard.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CommonErrorMessage {

    public String getMessage(int errorCode) {
        String resultMessage = StringUtils.EMPTY;
        switch (errorCode) {

            /* 변수 관련 에러 처리 */

            case ParameterException.PARAMETER_EXCEPTION:
                resultMessage = "변수에 문제가 있습니다 .";
                break;

            case ParameterException.NULL_PARAMETER_EXCEPTION:
                resultMessage = "변수가 누락되었습니다 .";
                break;

            case ParameterException.NULL_ID_PARAMETER_EXCEPTION:
                resultMessage = "ID 정보가 누락되었습니다 .";
                break;

            case ParameterException.NULL_PW_PARAMETER_EXCEPTION:
                resultMessage = "PW 정보가 누락되었습니다 .";
                break;

            case ParameterException.NULL_PHONE_PARAMETER_EXCEPTION:
                resultMessage = "핸드폰 번호 정보가 누락되었습니다 .";
                break;

            case ParameterException.NULL_EMAIL_PARAMETER_EXCEPTION:
                resultMessage = "이메일 정보가 누락되었습니다 .";
                break;

            case ParameterException.NULL_REGION_NUMBER_PARAMETER_EXCEPTION:
                resultMessage = "지역 번호 정보가 누락되었습니다 .";
                break;

            case ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION:
                resultMessage = "장비 시리얼 정보가 누락되었습니다 .";
                break;

            case ParameterException.NULL_LAT_LON_PARAMETER_EXCEPTION:
                resultMessage = "위도, 경도 정보가 누락되었습니다 .";
                break;

            case ParameterException.NULL_D_CODE_PARAMETER_EXCEPTION:
                resultMessage = "행정동 정보가 누락되었습니다 .";
                break;

            case ParameterException.NULL_TYPE_PARAMETER_EXCEPTION:
                resultMessage = "유형 선택 정보가 누락되었습니다 .";
                break;

            case ParameterException.WRONG_PARAMETER_EXCEPTION:
                resultMessage = "입력한 변수를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION:
                resultMessage = "입력한 ID 또는 PW 정보를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_PHONE_PARAMETER_EXCEPTION:
                resultMessage = "입력한 핸드폰 번호 정보를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_EMAIL_PARAMETER_EXCEPTION:
                resultMessage = "입력한 이메일 정보를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_REGION_NUMBER_PARAMETER_EXCEPTION:
                resultMessage = "입력한 지역 번호 정보를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_TELEPHONE_PARAMETER_EXCEPTION:
                resultMessage = "입력한 유선 전화 번호 정보를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_SERIAL_PARAMETER_EXCEPTION:
                resultMessage = "입력한 장비 시리얼 정보를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_LAT_LON_PARAMETER_EXCEPTION:
                resultMessage = "입력한 위도, 경도 정보를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_D_CODE_PARAMETER_EXCEPTION:
                resultMessage = "입력한 행정동 정보를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION:
                resultMessage = "입력한 유형 선택 정보를 확인하여 주십시오 .";
                break;

            case ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION:
              resultMessage = "입력한 명령어를 확인하여 주십시오 .";
              break;

            /* 인증 에러 처리 */

            // 쿠키 에러 처리
            case AuthException.AUTH_EXCEPTION:
                resultMessage = "인증 처리에 문제가 있습니다 .";
                break;

            case AuthException.PREMIUM_AUTH_EXCEPTION:
                resultMessage = "프리미엄 서비스 인증이 되지 않았습니다 .";
                break;

            case CookieAuthException.COOKIE_AUTH_DUPLICATION_EXCEPTION:
                resultMessage = "쿠키 인증이 중복되었습니다 .";
                break;

            case CookieAuthException.COOKIE_AUTH_NONE_EXCEPTION:
                resultMessage = "쿠키 인증이 없습니다 .";
                break;

            case CookieAuthException.COOKIE_AUTH_ADMIN_EXCEPTION:
                resultMessage = "관리자 쿠키 인증이 없습니다 .";
                break;

            case CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION:
                resultMessage = "일반 사용자 쿠키 인증이 없습니다 .";
                break;

            // 토큰 에러 처리
            case TokenAuthException.TOKEN_AUTH_EXCEPTION:
                resultMessage = "토큰 인증에 문제가 있습니다 .";
                break;

            case TokenAuthException.TOKEN_AUTH_DUPLICATION_EXCEPTION:
                resultMessage = "토큰 인증이 중복되었습니다 .";
                break;

            case TokenAuthException.TOKEN_AUTH_NONE_EXCEPTION:
                resultMessage = "토큰 인증이 없습니다 .";
                break;

            /* SQL 실행 에러 처리 */

            case SQLException.SQL_EXCEPTION:
                resultMessage = "SQL 실행 중 문제가 있습니다 .";
                break;

            case SQLException.NULL_TARGET_EXCEPTION:
                resultMessage = "목표가 존재하지 않습니다 .";
                break;

            case SQLException.DUPLICATE_TARGET_EXCEPTION:
                resultMessage = "목표가 중복되었습니다 .";
                break;

            case SQLException.LIMIT_TARGET_EXCEPTION:
                resultMessage = "목표에 접근할 수 없습니다 .";
                break;

            case SQLException.USER_LOGIN_ROCK_EXCEPTION:
                resultMessage = "로그인 5회 이상 실패하여 제한되었으므로 관리자에게 문의하여 주십시오 .";
                break;

            case SQLException.NOT_USE_DATA_EXCEPION:
              resultMessage = "사용 불가능한 데이터 입니다 .";
              break;

            case SQLException.REDIS_SQL_EXCEPTION:
                resultMessage = "Redis 사용 중 문제가 발생 하였습니다 .";
                break;

            case ExternalApiException.EXTERNAL_API_CALL_EXCEPTION:
              resultMessage = "H.W 장비 통신 중 문제가 발생 하였습니다 .";
              break;

            case ExternalApiException.PLATFORM_API_CALL_EXCEPTION:
                resultMessage = "플랫폼으로 측정데이터가 아직 수집되지 않았습니다. 잠시 기다려주세요";
                break;


        }

        return resultMessage;

    }

}
