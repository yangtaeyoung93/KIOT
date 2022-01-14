/**
 * COMMON
 */
//숫자만 ex)!re_num.test(f.userID.value)
const re_num = /^[0-9]*$/;
//문자+숫자 ex)re_txt_num.test(f.userID.value)
const re_txt_num = /([^A-Za-z0-9_]+)/;
//공백만금지 ex) re_blank.test(f.userID.value)
const re_blank = /^\s+|\s+$/g;
//공백체크 ex) re_blank1.test(f.userID.value)
const re_blank1 = /[\s]/g;
//특수문자체크 ex)re_spacial_pattern.test(f.userID.value)
const re_spacial_pattern = /[`~!@#$%^&*|\\\'\";:\/?]/gi;
//이메일체크 ex) !f.userID.value.match(re_email)
const re_email = /[0-9a-zA-Z][_0-9a-zA-Z-]*@[_0-9a-zA-Z-]+(\.[_0-9a-zA-Z-]+){1,2}$/;
//태그 체크 re_tag.test(f.userID.value)
const re_tag = /[<][^>]*[>]/gi;
//태그 제거 userid = userid.replace(re_tag,"");
//스크립트 스타일 체크
const re_script = new RegExp("(<script[^>]*>(.|[\\s\\r\\n])*<" + "/script>|<style[^>]*>(.|[\\s\\r\\n])*<" + "/style>)", "gim");
//스크립트 제거 userid = userid.replace(re_script,"");
//주석제거
const RegExpDS = /<!--[^>](.*?)-->/g;
//홈페이지 체크
const re_homepage = new RegExp("(http|https|ftp|telnet|news|irc)://([-/.a-zA-Z0-9_~#%$?&=:200-377()]+)", "gi")
// 비밀번호,아이디체크 영문,숫자만허용, 4~10자리 {}안에 숫자 바꿔주면됨
const re_txt_num_len = /^[A-Za-z0-9]{4,10}$/;
// 공백문자 개행문자만 입력 거절
const re = /^ss*$/;

const re_lat_lon = /^(?:-|-?(?:\d|[1-9]\d?|1(?:[0-7]\d?)?|1(?:80?)?)(?:\.[0-9]{0,30})?)$/;

function goFota() {
    const url = "http://fotasystem.kweather.co.kr/KETIKIOT";
    const name = "Fota FirmWare";
    const option = "width=1100, height=800, top=100, left=200";
    window.open(url, name, option);
}

function goKweatherLink(url) {
    const name = "Fota FirmWare";
    const option = "";
    window.open(url, name, option);
}

// aJax 이동
function pageAjaxMove(url) {
    const ajaxOption = {
        url: url,
        async: true,
        type: "GET",
        dataType: "html",
        cache: false
    };

    $.ajax(ajaxOption).done(function (data) {
        // mainContent 영역 삭제
        $('#mainContent').children().remove();
        // mainContent 영역 교체
        $('#mainContent').html(data);
    });
}

function menuActiveEvt() {
    const oneDepth = $("#oneDepth").val();
    const twoDepth = $("#twoDepth").val();
    const threeDepth = $("#threeDepth").val();

    if (oneDepth) {
        if (oneDepth == "dashboard") {
            $("#dashboard").addClass("menu-open");
            $("#dashboard_link").addClass("active");
        }
        if (oneDepth == "collection") {
            $("#collection").addClass("menu-open");
            $("#collection_link").addClass("active");
        }
        if (oneDepth == "monitor") {
            $("#monitor").addClass("menu-open");
            $("#monitor_link").addClass("active");
        }
        if (oneDepth == "dong") {
            $("#dong").addClass("menu-open");
            $("#dong_link").addClass("active");
        }
        if (oneDepth == "air365") {
            $("#air365").addClass("menu-open");
            $("#air365_link").addClass("active");
        }
        if (oneDepth == "system") {
            $("#system").addClass("menu-open");
            $("#system_link").addClass("active");
        }
    }

    if (twoDepth) {
        if (oneDepth == "dashboard" && twoDepth == "receive") {
            $("#dashboard_receive").addClass("active");
        }
        if (oneDepth == "dashboard" && twoDepth == "user") {
            $("#dashboard_user").addClass("active");
        }
        if (oneDepth == "dashboard" && twoDepth == "equi") {
            $("#dashboard_equi").addClass("active");
        }
        if (oneDepth == "dashboard" && twoDepth == "dust") {
            $("#dashboard_dust").addClass("active");
        }

        if (oneDepth == "collection" && twoDepth == "iaq") {
            $("#collection_iaq").addClass("active");
        }
        if (oneDepth == "collection" && twoDepth == "oaq") {
            $("#collection_oaq").addClass("active");
        }
        if (oneDepth == "collection" && twoDepth == "dot") {
            $("#collection_dot").addClass("active");
        }
        if (oneDepth == "collection" && twoDepth == "vent") {
            $("#collection_vent").addClass("active");
        }
        if (oneDepth == "collection" && twoDepth == "connect") {
            $("#collection_connect").addClass("active");
        }
        if (oneDepth == "collection" && twoDepth == "dataDownload") {
            $("#collection_dataDownload").addClass("active");
        }

        if (oneDepth == "monitor" && twoDepth == "iaq") {
            $("#monitor_iaq").addClass("active");
        }
        if (oneDepth == "monitor" && twoDepth == "oaq") {
            $("#monitor_oaq").addClass("active");
        }
        if (oneDepth == "monitor" && twoDepth == "dot") {
            $("#monitor_dot").addClass("active");
        }
        if (oneDepth == "monitor" && twoDepth == "fota") {
            $("#monitor_fota").addClass("active");
        }

        if (oneDepth == "dong" && twoDepth == "regionDev") {
            $("#dong_regionDev").addClass("active");
        }
        if (oneDepth == "dong" && twoDepth == "data") {
            $("#dong_data").addClass("active");
        }
        if (oneDepth == "dong" && twoDepth == "airKor") {
            $("#dong_airKor").addClass("active");
        }
        if (oneDepth == "dong" && twoDepth == "offsetRegion") {
            $("#dong_offsetRegion_link").addClass("menu-open");
            $("#dong_offsetRegion").addClass("active");
        }
        if (oneDepth == "dong" && twoDepth == "offsetData") {
            $("#dong_offsetData_link").addClass("menu-open");
            $("#dong_offsetData").addClass("active");
        }
        if (oneDepth == "dong" && twoDepth == "dataDownload") {
            $("#dong_dataDownload").addClass("active");
        }

        if (oneDepth == "system" && twoDepth == "member") {
            $("#system_member_link").addClass("menu-open");
            $("#system_member").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "group") {
            $("#system_group_link").addClass("menu-open");
            $("#system_group").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "device") {
            $("#system_device_link").addClass("menu-open");
            $("#system_device").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "test") {
            $("#system_test_link").addClass("menu-open");
            $("#system_test").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "fota") {
            $("#system_fota_link").addClass("menu-open");
            $("#system_fota").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "type") {
            $("#system_type").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "model") {
            $("#system_model").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "attribute") {
            $("#system_attribute").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "elements") {
            $("#system_elements").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "space") {
            $("#system_space").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "admin") {
            $("#system_admin_link").addClass("menu-open");
            $("#system_admin").addClass("active");
        }

        if (oneDepth == "air365" && twoDepth == "pushMessage") {
            $("#air365_pushMessage").addClass("active");
        }
    }

    if (threeDepth) {
        if (oneDepth == "dong" && twoDepth == "offsetRegion" && threeDepth == "OAQ") {
            $("#dong_offsetRegion_OAQ").addClass("active");
        }
        if (oneDepth == "dong" && twoDepth == "offsetRegion" && threeDepth == "DOT") {
            $("#dong_offsetRegion_DOT").addClass("active");
        }

        if (oneDepth == "dong" && twoDepth == "offsetData" && threeDepth == "OAQ") {
            $("#dong_offsetData_OAQ").addClass("active");
        }
        if (oneDepth == "dong" && twoDepth == "offsetData" && threeDepth == "DOT") {
            $("#dong_offsetData_DOT").addClass("active");
        }

        if (oneDepth == "system" && twoDepth == "member" && threeDepth == "user") {
            $("#system_member_user").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "member" && threeDepth == "device") {
            $("#system_member_device").addClass("active");
        }

        if (oneDepth == "system" && twoDepth == "group" && threeDepth == "user") {
            $("#system_group_user").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "group" && threeDepth == "did") {
            $("#system_group_did").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "group" && threeDepth == "custom") {
            $("#system_group_custom").addClass("active");
        }

        if (oneDepth == "system" && twoDepth == "device" && threeDepth == "IAQ") {
            $("#system_device_IAQ").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "device" && threeDepth == "OAQ") {
            $("#system_device_OAQ").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "device" && threeDepth == "DOT") {
            $("#system_device_DOT").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "device" && threeDepth == "VENT") {
            $("#system_device_VENT").addClass("active");
        }

        if (oneDepth == "system" && twoDepth == "test" && threeDepth == "IAQ") {
            $("#system_test_IAQ").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "test" && threeDepth == "OAQ") {
            $("#system_test_OAQ").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "test" && threeDepth == "DOT") {
            $("#system_test_DOT").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "test" && threeDepth == "VENT") {
            $("#system_test_VENT").addClass("active");
        }

        if (oneDepth == "system" && twoDepth == "admin" && threeDepth == "admin") {
            $("#system_admin_admin").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "admin" && threeDepth == "menu") {
            $("#system_admin_menu").addClass("active");
        }
        if (oneDepth == "system" && twoDepth == "admin" && threeDepth == "menuAuth") {
            $("#system_admin_menuAuth").addClass("active");
        }
    }
}

function menuDraw(oneDatas, twoDatas, threeDatas) {
    let menuListHTML = "";
    let threeFlag = false;
    let threeHTML = "";

    let idx1 = 0;

    for (let oneIdx in oneDatas) {
        idx1++;
        if (oneDatas[oneIdx].menuAuth == 0) continue;

        menuListHTML +=
            '<li class="nav-item has-treeview" id="' + oneDatas[oneIdx].menuEng +
            '" onclick="' + (oneDatas[oneIdx].menuUrl == '#' ? 'null' : oneDatas[oneIdx].menuUrl) + '"><a href="#" class="nav-link" id="' + oneDatas[oneIdx].menuEng + '_link">' +
            '<i class="nav-icon fas ' + oneDatas[oneIdx].menuTag + '"></i><p> ' + oneDatas[oneIdx].menuName +
            ' <i class="right fas fa-angle-left"></i></p></a><ul class="nav nav-treeview">';

        let idx2 = 0;
        for (let twoIdx in twoDatas) {
            if (oneDatas[oneIdx].idx == twoDatas[twoIdx].highRankMenu) {
                idx2++;
                if (twoDatas[twoIdx].menuAuth == 0) continue;

                menuListHTML +=
                    '<li class="nav-item" id="' + oneDatas[oneIdx].menuEng + '_' + twoDatas[twoIdx].menuEng + '_link"><a href="' + twoDatas[twoIdx].menuUrl +
                    '" class="nav-link" id="' + oneDatas[oneIdx].menuEng + '_' + twoDatas[twoIdx].menuEng + '"><i class="' + twoDatas[twoIdx].menuTag + ' nav-icon"></i>' +
                    '<p>' + twoDatas[twoIdx].menuName;

                let idx3 = 0;
                for (let threeIdx in threeDatas) {
                    if (twoDatas[twoIdx].idx == threeDatas[threeIdx].highRankMenu) {
                        idx3++;
                        if (threeDatas[threeIdx].menuAuth == 0) continue;

                        threeFlag = true;
                        if (idx3 == 1) threeHTML += '<ul class="nav nav-treeview">';
                        threeHTML +=
                            '<li class="nav-item"><a href="' + threeDatas[threeIdx].menuUrl + '" class="nav-link" id="' +
                            oneDatas[oneIdx].menuEng + '_' + twoDatas[twoIdx].menuEng + '_' + threeDatas[threeIdx].menuEng + '"><i class="nav-icon ' + threeDatas[threeIdx].menuTag + '"></i>' +
                            '<p>' + threeDatas[threeIdx].menuName + '</p></a></li>';
                    }
                }

                if (threeFlag) {
                    threeHTML += '</ul>';
                    menuListHTML += '<i class="right fas fa-angle-left"></i></p></a>';
                    menuListHTML += threeHTML;
                    threeHTML = "";
                    threeFlag = false;
                }

                menuListHTML += '</li>';
            }
        }

        menuListHTML += '</ul></li>'
    }

    $("#menuListUl").html(menuListHTML);
    menuActiveEvt();
}

function getMenuList() {
    const oneLevelMenu = new Array();
    const twoLevelMenu = new Array();
    const threeLevelMenu = new Array();

    $.ajax({
        method: "GET",
        url: "/system/admin/menu/data",
        contentType: "application/json; charset=utf-8",
        success: function (d) {
            if (d != null && d.data != null) {
                for (let i = 0; i < d.data.length; i++) {
                    if (d.data[i].menuLevel == 1) oneLevelMenu.push(d.data[i]);
                    else if (d.data[i].menuLevel == 2) twoLevelMenu.push(d.data[i]);
                    else if (d.data[i].menuLevel == 3) threeLevelMenu.push(d.data[i]);
                }

                menuDraw(oneLevelMenu, twoLevelMenu, threeLevelMenu);
            }
        }
    })
}

$().ready(function () {
    getMenuList();

    const oneDepth = $("#oneDepth").val();
    const twoDepth = $("#twoDepth").val();
    const threeDepth = $("#threeDepth").val();

    $("#all_chk").click(function () {
        if ($("#all_chk").prop("checked")) {
            $("input[name=boardIdx]").attr("checked", true);
        } else {
            $("input[name=boardIdx]").attr("checked", false);
        }
    })
})

function txtColor(val, receiveFlag) {
    let colorCode = "";

    if (receiveFlag == false) {
        colorCode = "#929498";
    } else {
        if (val >= 0 && val <= 49) {
            colorCode = "#ff0000";
        } else if (val >= 50 && val <= 79) {
            colorCode = "#ff6700";
        } else if (val >= 80 && val <= 89) {
            colorCode = "#8dd638";
        } else if (val >= 90 && val <= 100) {
            colorCode = "#44c7f4";
        } else {
            colorCode = "#929498";
        }
    }
    return colorCode;
}

function txtCO2Color(val, receiveFlag) {
    let colorCode = "";

    if (receiveFlag == false) {
        colorCode = "#929498";
    } else {
        if (val >= 1501 && val <= 10000) {
            colorCode = "#ff0000";
        } else if (val >= 1001 && val <= 1500) {
            colorCode = "#ff6700";
        } else if (val >= 501 && val <= 1000) {
            colorCode = "#8dd638";
        } else if (val >= 0 && val <= 500) {
            colorCode = "#44c7f4";
        } else {
            colorCode = "#929498";
        }
    }
    return colorCode;
}

function txtVOCColor(val, receiveFlag) {
    let colorCode = "";

    if (receiveFlag == false) {
        colorCode = "#929498";
    } else {
        if (val >= 1001 && val <= 10000) {
            colorCode = "#ff0000";
        } else if (val >= 1001 && val <= 1500) {
            colorCode = "#ff6700";
        } else if (val >= 500 && val <= 1000) {
            colorCode = "#8dd638";
        } else if (val >= 0 && val <= 500) {
            colorCode = "#44c7f4";
        } else {
            colorCode = "#929498";
        }
    }
    return colorCode;
}

function txtNOISEColor(val, receiveFlag) {
    let colorCode = "";
    if (receiveFlag == false) {
        colorCode = "#929498";
    } else {
        if (val >= 71 && val <= 100) {
            colorCode = "#ff0000";
        } else if (val >= 56 && val <= 70) {
            colorCode = "#ff6700";
        } else if (val >= 31 && val <= 55) {
            colorCode = "#8dd638";
        } else if (val >= 0 && val <= 30) {
            colorCode = "#44c7f4";
        } else {
            colorCode = "#929498";
        }
    }
    return colorCode;
}