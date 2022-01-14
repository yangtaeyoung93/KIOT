/**
 * 수집정보 > IAQ 수집정보
 */
let historyData;

$().ready(
    function () {
        $("#startDt").val("");
        $("#endDt").val("");

        $("#dotTable_filter").hide();

        $("#searchTestYn").click(function () {
            const chkTestYn = $("#searchTestYn");
            if (chkTestYn.prop("checked") == true) {
                $(this).val("Y");
            } else {
                $(this).val("N");
            }
        })

        if (!$("#startDt").val()) {
            const now_date = new Date();
            const now_year = now_date.getFullYear();
            let now_month = now_date.getMonth() + 1;
            let now_day = now_date.getDate();

            if (now_month < 10) {
                now_month = "0" + now_month;
            }
            if (now_day < 10) {
                now_day = "0" + now_day;
            }

            $("#startDt").val(now_year + "/" + now_month + "/" + now_day);
        }
        if (!$("#endDt").val()) {
            const now_date = new Date();
            const now_year = now_date.getFullYear();
            let now_month = now_date.getMonth() + 1;
            let now_day = now_date.getDate();

            if (now_month < 10) {
                now_month = "0" + now_month;
            }

            if (now_day < 10) {
                now_day = "0" + now_day;
            }

            $("#endDt").val(now_year + "/" + now_month + "/" + now_day);
        }

        viewData(serialNum, parentSpaceName, spaceName, productDt,
            stationName, standard);

        $("#modalSearchBtn").click(
            function () {

                const serialNum = $("#p_h_serialNum").val();
                const parentSpaceName = $("#p_h_parentSpaceName").val();
                const spaceName = $("#p_h_spaceName").val();
                const productDt = $("#p_h_productDt").val();
                const stationName = $("#p_h_stationName").val();
                const standard = $("#searchStandard option:selected")
                    .val();

                viewData(serialNum, parentSpaceName, spaceName,
                    productDt, stationName, standard);
            })

        const rangeDate = 31; // set limit day
        let setSdate, setEdate;

        $("#startDt").datepicker({
            dateFormat: 'yy/mm/dd',
            onSelect: function (selectDate) {
                const stxt = selectDate.split("/");
                stxt[1] = stxt[1] - 1;
                const sdate = new Date(stxt[0], stxt[1], stxt[2]);
                const edate = new Date(stxt[0], stxt[1], stxt[2]);
                edate.setDate(sdate.getDate() + rangeDate);

                $('#endDt').datepicker('option', {
                    minDate: selectDate,
                    beforeShow: function () {
                        $("#endDt").datepicker("option", "maxDate", edate);
                        setSdate = selectDate;
                    }
                });
                // to 설정
            }
        })

        $("#endDt").datepicker({
            dateFormat: 'yy/mm/dd',
            onSelect: function (selectDate) {
                setEdate = selectDate;
            }
        })

        $('#chartTypes').click(function () {
            chgChartData();
        })

        $('#modalDownloadBtn').click(function () {
            $('.excelDownBtn').trigger('click');
        })
    })

function chgChartData() {
    const chkArr = Array();
    const obj = document.getElementsByName("chartType");

    for (let i = 0; i < obj.length; i++)
        if (document.getElementsByName("chartType")[i].checked == true)
            chkArr.push(obj[i].value);

    timeChartCreate("timeChartDiv", historyData, chkArr);
}

function timeChartCreate(chartDiv, hisData, options) {
    am4core.addLicense("CH205407412");
    // am4core.useTheme(am4themes_animated);

    hisData = hisData.data.sort(function (a, b) {
        return a.timestamp < b.timestamp ? -1 : a.timestamp > b.timestamp ? 1
            : 0;
    });

    const chart = am4core.create(chartDiv, am4charts.XYChart);
    chart.data = hisData;
    chart.cursor = new am4charts.XYCursor();
    chart.cursor.behavior = "panXY";
    chart.scrollbarY = new am4core.Scrollbar();
    chart.scrollbarY.parent = chart.leftAxesContainer;
    chart.scrollbarY.toBack();
    chart.scrollbarX = new am4charts.XYChartScrollbar();
    chart.scrollbarX.parent = chart.topAxesContainer;

    const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = "convertTime";
    categoryAxis.renderer.opposite = false;

    for (let idx in options)
        createSeries(options[idx]);

    function chgOptionNameFunc(name) {
        let chgName = "";

        if (name == "pm10")
            chgName = "미세먼지 (㎍/㎥)";
        else if (name == "pm25")
            chgName = "초미세먼지 (㎍/㎥)";
        else if (name == "co2")
            chgName = "이산화탄소 (ppm)";
        else if (name == "voc")
            chgName = "휘발성유기화합물 (ppb)";
        else if (name == "noise")
            chgName = "소음 (dB)";
        else if (name == "temp")
            chgName = "온도 (℃ )";
        else if (name == "humi")
            chgName = "습도 (%)";
        else if (name == "cici")
            chgName = "통합지수 (cici)";
        else
            chgName = name;

        return chgName;
    }

    function createSeries(name) {
        const chgName = chgOptionNameFunc(name);

        const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
        valueAxis.renderer.inversed = false;
        valueAxis.title.text = chgName;
        valueAxis.renderer.minLabelPosition = 0.01;

        const interfaceColors = new am4core.InterfaceColorSet();

        const series = chart.series.push(new am4charts.LineSeries());
        series.dataFields.valueY = name;
        series.dataFields.categoryX = "convertTime";
        series.yAxis = valueAxis;
        series.name = chgName;
        series.tensionX = 0.8;
        series.showOnInit = true;

        const segment = series.segments.template;
        segment.interactionsEnabled = true;

        const hoverState = segment.states.create("hover");
        hoverState.properties.strokeWidth = 3;

        const dimmed = segment.states.create("dimmed");
        dimmed.properties.stroke = am4core.color("#dadada");

        series.tooltip.background.cornerRadius = 20;
        series.tooltip.background.strokeOpacity = 0;
        series.tooltip.pointerOrientation = "vertical";
        series.tooltip.label.minWidth = 40;
        series.tooltip.label.minHeight = 40;
        series.tooltip.label.textAlign = "middle";
        series.tooltip.label.textValign = "middle";
        series.tooltipText = "{name}: [bold]{valueY}[/]";

        series.strokeWidth = 3;

        series.connect = false;

        chart.scrollbarX.series.push(series);

        valueAxis.renderer.line.stroke = series.stroke;
        valueAxis.renderer.labels.template.fill = series.stroke;
        valueAxis.renderer.opposite = true;

        valueAxis.renderer.line.strokeOpacity = 1;
        valueAxis.renderer.line.strokeWidth = 2;
        valueAxis.renderer.line.stroke = series.stroke;
        valueAxis.renderer.labels.template.fill = series.stroke;
        valueAxis.renderer.opposite = true;
    }

    chart.legend = new am4charts.Legend();
}

let fieldDatas;
let fieldColums;

function popDataTableDraw(apiData, rawApiData, colums) {
    fieldDatas = apiData;
    let columsData = [];

    for (let key in colums) {
        columsData.push({
            "data": colums[key]
        });
    }

    fieldColums = columsData;

    const h_serialNum = $("#p_h_serialNum").val();
    const h_parentSpaceName = $("#p_h_parentSpaceName").val();
    const h_spaceName = $("#p_h_spaceName").val();
    const h_productDt = $("#p_h_productDt").val();
    const h_stationName = $("#p_h_stationName").val();
    let spaceStr = "";

    if (!h_parentSpaceName && !h_spaceName) {
        spaceStr = "없음";
    } else if (h_parentSpaceName && !h_spaceName) {
        spaceStr = h_parentSpaceName;
    } else if (!h_parentSpaceName && h_spaceName) {
        spaceStr = h_spaceName;
    }

    $("#txtSerialNum").text(h_serialNum);
    $("#txtSpace").text(spaceStr);
    $("#txtProductDt").text(h_productDt);
    $("#txtStationName").text(h_stationName + "의 ");

    const table = $('#popdotTable').DataTable(
        {
            scrollCollapse: true,
            autoWidth: false,
            language: {
                "emptyTable": "데이터가 없습니다.",
                "lengthMenu": "페이지당 _MENU_ 개씩 보기",
                "info": "현재 _START_ - _END_ / _TOTAL_건",
                "infoEmpty": "데이터 없음",
                "infoFiltered": "( _MAX_건의 데이터에서 필터링됨 )",
                "search": "",
                "zeroRecords": "일치하는 데이터가 없습니다.",
                "loadingRecords": "로딩중...",
                "processing": "잠시만 기다려 주세요.",
                "paginate": {
                    "next": "다음",
                    "previous": "이전"
                }
            },
            dom: '<"top"Blf>rt<"bottom"ip><"clear">',
            responsive: false,
            scrollX: true,
            buttons: [{
                extend: 'csv',
                charset: 'UTF-8',
                text: '엑셀 다운로드',
                footer: false,
                bom: true,
                filename: h_serialNum + '_download',
                className: 'btn-primary btn excelDownBtn'
            }],
            destroy: true,
            processing: true,
            serverSide: false,
            data: fieldDatas,
            columns: fieldColums,
            columnDefs: [
                {
                    targets: 0,
                    render: function (data, type, full, meta) {
                        let date = "";
                        let convertTime = "";

                        if (data == null) {
                            convertTime = "";
                        } else {
                            date = new Date(data * 1000);
                            const year = date.getFullYear();

                            // 내일 날짜부분 고치기
                            let month = date.getMonth() + 1;
                            let day = date.getDate();
                            let hours = date.getHours();
                            let minutes = date.getMinutes();
                            let seconds = date.getSeconds();

                            if (month < 10) {
                                month = '0' + month;
                            }
                            if (day < 10) {
                                day = '0' + day;
                            }

                            if (hours < 10) {
                                hours = '0' + hours;
                            }
                            if (minutes < 10) {
                                minutes = '0' + minutes;
                            }
                            if (seconds < 10) {
                                seconds = '0' + seconds;
                            }

                            convertTime = year + "-" + month + "-"
                                + day + " " + hours + ":" + minutes
                                + ":" + seconds;
                        }
                        return convertTime;
                    },
                }, {
                    targets: 1, // 데이터테이블 자체 RowNum
                    visible: false,
                    render: function (data, type, full, meta) {
                        return "";
                    },
                },],
            initComplete: function (settings, data) {
                historyData = rawApiData;
                for (let i = 0; i < historyData.data.length; i++) {
                    const formatTimestamp = historyData.data[i].timestamp;
                    let date = "";
                    let convertTime = "";

                    if (historyData.data[i].timestamp == null) {
                        convertTime = "";
                    } else {
                        date = new Date(
                            historyData.data[i].timestamp * 1000);
                        const year = date.getFullYear();
                        let month = date.getMonth() + 1;
                        let day = date.getDate();

                        let hours = date.getHours();
                        let minutes = date.getMinutes();
                        let seconds = date.getSeconds();

                        if (month < 10) {
                            month = '0' + month;
                        }
                        if (day < 10) {
                            day = '0' + day;
                        }

                        if (hours < 10) {
                            hours = '0' + hours;
                        }
                        if (minutes < 10) {
                            minutes = '0' + minutes;
                        }
                        if (seconds < 10) {
                            seconds = '0' + seconds;
                        }

                        convertTime = year + "-" + month + "-" + day + "\n"
                            + hours + ":" + minutes;
                    }
                    historyData.data[i].convertTime = convertTime;
                }
                chgChartData();
            }
        });

    $("#popdotTable_filter").hide();
    $($("#tableTitle")).insertBefore("#popdotTable_length");
    $("#popdotTable_length").css("display", "inline-block");
    $("#popdotTable_length").css("margin-left", "20px");
    $("#modalDownloadBtn").insertAfter("#popdotTable_filter");
    $("#popdotTable_wrapper .top").first().css("border-bottom",
        "1px solid #E5E5E5");
    $("#popdotTable_wrapper .top").first().css("padding-bottom", "15px");
    $("#popdotTable_wrapper .top").first().css("margin-bottom", "20px");
    $('.excelDownBtn').css("float", "right");
}

function columNameChg(name, brFlag) {
    let chgName = "";
    const brHtml = (brFlag) ? "<br/>" : "";

    switch (name) {
        case "formatTimestamp":
            chgName = "데이터 시간";
            break;
        case "timestamp":
            chgName = "데이터 시간";
            break;
        case "pm10":
            chgName = "미세먼지" + brHtml + "(㎍/㎥) ";
            break;
        case "pm25":
            chgName = "초미세먼지" + brHtml + "(㎍/㎥) ";
            break;
        case "pm01":
            chgName = "극초미세먼지" + brHtml + "(㎍/㎥) ";
            break;
        case "pm01_raw":
            chgName = "극초미세먼지 Raw";
            break;
        case "pm25_raw":
            chgName = "초미세먼지 Raw";
            break;
        case "pm10_raw":
            chgName = "미세먼지 Raw";
            break;
        case "temp":
            chgName = "온도" + brHtml + "(℃)";
            break;
        case "humi":
            chgName = "습도" + brHtml + "(%)";
            break;
        case "co2":
            chgName = "이산화탄소" + brHtml + "(ppm)";
            break;
        case "voc":
            chgName = "휘발성유기화합물" + brHtml + "(ppb)";
            break;
        case "noise":
            chgName = "소음" + brHtml + "(dB)";
            break;
        case "lux":
            chgName = "조도" + brHtml + "(lx)";
            break;
        case "uv":
            chgName = "자외선" + brHtml + "(UVI)";
            break;
        case "wbgt":
            chgName = "흑구온도" + brHtml + "(℃)";
            break;
        case "co":
            chgName = "일산화탄소" + brHtml + "(ppm)";
            break;
        case "hcho":
            chgName = "포름알데히드" + brHtml + "(ppm)";
            break;
        case "o3":
            chgName = "오존" + brHtml + "(ppm)";
            break;
        case "rn":
            chgName = "라돈" + brHtml + "(pCi/l)";
            break;
        case "no2":
            chgName = "이산화질소" + brHtml + "(ppm)";
            break;
        case "so2":
            chgName = "이산화황" + brHtml + "(ppm)";
            break;
        case "accx":
            chgName = "진동x" + brHtml + "(g)";
            break;
        case "accx_max":
            chgName = "진동x 최대" + brHtml + "(g)";
            break;
        case "accy":
            chgName = "진동y" + brHtml + "(g)";
            break;
        case "accy_max":
            chgName = "진동y_최대" + brHtml + "(g)";
            break;
        case "accz":
            chgName = "진동z" + brHtml + "(g)";
            break;
        case "accz_max":
            chgName = "진동z_최대" + brHtml + "(g)";
            break;
        case "windd":
            chgName = "풍향" + brHtml + "(°)";
            break;
        case "windd_max":
            chgName = "돌풍 풍향" + brHtml + "(°)";
            break;
        case "winds":
            chgName = "풍속" + brHtml + "(m/s)";
            break;
        case "winds_max":
            chgName = "돌풍 풍속" + brHtml + "(m/s)";
            break;
        case "cici":
            chgName = "cici" + brHtml + "(CICI)";
            break;
        case "cici_pm10":
            chgName = "cici_pm10";
            break;
        case "cici_pm25":
            chgName = "cici_pm25";
            break;
        case "cici_co2":
            chgName = "cici_co2";
            break;
        case "cici_voc":
            chgName = "cici_voc";
            break;
        case "cici_temp":
            chgName = "cici_temp";
            break;
        case "cici_humi":
            chgName = "cici_humi";
            break;
        case "cici_noise":
            chgName = "cici_noise";
            break;
        case "coci":
            chgName = "통합지수" + brHtml + "(COCI)";
            break;
        case "coci_pm10":
            chgName = "coci_pm10";
            break;
        case "coci_pm25":
            chgName = "coci_pm25";
            break;
        case "coci_humi":
            chgName = "coci_humi";
            break;
        case "coci_temp":
            chgName = "coci_temp";
            break;
        case "pm10_offset":
            chgName = "pm10_offset";
            break;
        case "pm25_offset":
            chgName = "pm25_offset";
            break;
        case "pm10_ratio":
            chgName = "pm10_ratio";
            break;
        case "pm25_ratio":
            chgName = "pm25_ratio";
            break;
        case "ai_mode_devices":
            chgName = "미세먼지";
            break;
        case "power":
            chgName = "전원 상태";
            break;
        case "air_volume":
            chgName = "풍량";
            break;
        case "exh_mode":
            chgName = "배기모드";
            break;
        case "auto_mode":
            chgName = "자동모드";
            break;
        case "filter_alarm":
            chgName = "필터알림";
            break;
        case "air_mode":
            chgName = "공기청정기모드";
            break;
        default:
            chgName = name;
            break;
    }

    return chgName;
}

function tableTrDraw(datas, rawDatas, colums) {
    colums.splice(1, 1);

    $('#popTableHead').html("");
    $('#popTableBody').html("");
    $('#chartTypeSelect').html("");

    let orderIdx = 0;
    let trHeadHTML = "";
    let trBodyHTML = "";
    let chartTypesHTML = "";

    for (let i = 0; i < colums.length; i++) {
        trHeadHTML += "<th style='width: 200px;' class='bgGray1'>"
            + columNameChg(colums[i], true) + "</th>";
        trBodyHTML += "<td></td>";

        if (colums[i] != "timestamp" && colums[i] != "tm"
            && colums[i] != "pm01_raw" && colums[i] != "pm10_raw"
            && colums[i] != "pm25_raw") {
            chartTypesHTML += "<label style='padding-right: 10px'><input type='checkbox' name='chartType' value='"
                + colums[i]
                + "'>"
                + columNameChg(colums[i], false)
                + "</label>";

            orderIdx++;

            if (orderIdx % 10 == 0)
                chartTypesHTML += "<br/>";
        }
    }

    $('#popTableHead').html(trHeadHTML);
    $('#popTableBody').html(trBodyHTML);
    $('#chartTypes').html(chartTypesHTML);

    popDataTableDraw(datas, rawDatas, colums);
}

function viewData(serialNum, parentSpaceName, spaceName, productDt,
                  stationName, standard) {
    let apiData;
    let obj = new Object();
    $('#modal-lg').modal();
    let startTime = $("#startDt").val();
    let endTime = $("#endDt").val();

    if (!startTime && !endTime) {
        const today = new Date();
        const year = today.getFullYear(); // 년도
        let month = today.getMonth() + 1; // 월
        let day = today.getDate(); // 날짜

        if (month < 10) {
            month = "0" + month;
        }
        if (day < 10) {
            day = "0" + day;
        }

        startTime = year + "/" + month + "/" + day + "-00:00:00";
        endTime = year + "/" + month + "/" + day + "-23:59:59";
    } else {
        startTime = startTime + "-00:00:00";
        endTime = endTime + "-23:59:59";
    }

    $("#p_h_serialNum").val(serialNum);
    $("#p_h_parentSpaceName").val(parentSpaceName);
    $("#p_h_spaceName").val(spaceName);
    $("#p_h_productDt").val(productDt);
    $("#p_h_stationName").val(stationName);

    if (!standard) {
        standard = "sum";
    }

    obj = {
        deviceType: "dot", // 사용자 계정
        serial: $("#p_h_serialNum").val(), // 장비idx
        startTime: startTime, // 2020/04/23-00:00:00
        endTime: endTime, // 2020/04/23-23:59:59
        standard: standard,
        connect: "0"
    }

    let collectionColum = new Array();
    $.ajax({
        method: "GET",
        url: "/api/collection/history",
        data: obj,
        success: function (d) {
            apiData = d.data;
            if (apiData.length != 0) {
                for (let i = 0; i < apiData.length; i++)
                    for (const key in apiData[i])
                        if (apiData[i][key] != null)
                            if (key != "pm10_raw" && key != "pm25_raw"
                                && key != "pm01_raw"
                                && key != "ai_mode_devices")
                                collectionColum.push(key);
            } else
                alert("수집된 데이터가 없습니다.");

            collectionColum = collectionColum.reduce(function (a, b) {
                if (a.indexOf(b) < 0)
                    a.push(b);
                return a;
            }, []);

            if (collectionColum.length != 0)
                tableTrDraw(apiData, d, collectionColum);
        }
    });
}