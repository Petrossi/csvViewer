$( document ).ready(function () {
    initTest();
    // clearAll();
})

function clearAll() {
    assignText("");
}

function initTest() {
    const testCSV = "39,2092,2,4,2019-10-21 16:19:32,\n" +
        "40,2093,2,4,2019-10-21 16:19:44,\n" +
        "41,2094,0,4,2019-10-21 16:24:53,\n" +
        "42,2100,10138923,4,2019-10-21 16:34:07,\n" +
        "43,2101,10138923,4,2019-10-21 16:40:49,\n" +
        "44,2104,10138923,4,2019-10-21 17:22:54,\n" +
        "45,2105,10138923,4,2019-10-21 17:28:26,\n" +
        "46,2106,10138923,4,2019-10-21 17:29:10,\n" +
        "47,2110,10138923,4,2019-10-22 10:03:21,\n" +
        "48,2114,10138923,4,2019-10-22 10:20:43,\n" +
        "49,2116,10138923,4,2019-10-22 10:23:01,\n" +
        "50,2118,2,4,2019-10-22 10:45:38,\n" +
        "51,2125,2,4,2019-10-22 11:20:48,\n" +
        "52,2126,10138923,4,2019-10-22 11:21:20,\n" +
        "53,2127,10138923,4,2019-10-22 11:21:42,\n" +
        "54,2128,10138923,4,2019-10-22 11:22:41,\n" +
        "55,2129,10138923,4,2019-10-22 11:24:33,\n" +
        "56,2130,10009172,4,2019-10-22 11:58:47,\n" +
        "57,2132,0,4,2019-10-22 13:01:14,"

    assignText(testCSV);
}

function assignText(s) {
    document.getElementById('txt1').value = s;
    reprocessCsv();
}

function parseAndSort() {
    spinit.spin(document.getElementById('divSpinner'));
    parse(CSV);
    showGrid();
    spinit.stop(document.getElementById('divSpinner'));
}

function parse(CSV) {
    var dateFound = 0;

    CSV.parse(document.getElementById(`txt1`).value);
    if (false && CSV[`detectedQuote`] !== CSV[`quote`]) {
        CSV.quote = CSV.detectedQuote;
        CSV.parse(document.getElementById(`txt1`).value);
        if (document[`getElementById`](`chkInputQuote`)) {
            document[`getElementById`](`chkInputQuote`)[`checked`] = CSV[`detectedQuote`] === `'`
        }
    }
    for (j = 0; j < CSV.maxColumnsFound; j++) {
        if (CSV[`statsCnt`][j] && CSV[`statsCnt`][j][`fieldType`] === `D`) {
            dateFound++
        }
    }
    if (detCsvDateFormat && dateFound > 0) {
        detCsvDateFormat(CSV)
    } else {
        CSV[`dateformat`] = undefined
    }
}

function detCsvDateFormat(CSV) {
    var i, j, k;
    var v;
    var fmts = ["MM/DD/YYYY", "DD/MM/YYYY", "YYYY/MM/DD", "MM/DD/YY", "DD/MM/YY", "MM-DD-YYYY", "DD-MM-YYYY", "YYYY-MM-DD", "MM-DD-YY", "DD-MM-YY", "MMM DD YYYY", "DD MMM YYYY", "DD MMM YY"];
    var fmtc = [];
    for (i = 0; i < fmts.length; i++) fmtc.push(0); // counts
    CSV.dateformat = [];
    for (k = 0; k < CSV.maxColumnsFound; k++) CSV.dateformat.push("");
    for (k = 0; k < CSV.maxColumnsFound; k++) {
        if (CSV.statsCnt[k] && CSV.statsCnt[k].fieldType === "D") {
            //alert("detCsvDateFormat: checking date format at " + k);
            for (j = 0; j < CSV.table.length; j++) { // for each row
                v = CSV.table[j][k] || "";
                for (i = 0; i < fmts.length; i++) {
                    //alert("date=" + v + ",format=" + fmts[i]);
                    if(moment(v,fmts[i],true).isValid()) fmtc[i]++;
                }
            }
            //alert(JSON.stringify(fmtc));
            // find index of largest count
            var max = fmtc[0];
            var maxIndex = 0;

            for (i = 1; i < fmtc.length; i++) {
                if (fmtc[i] > max) {
                    maxIndex = i;
                    max = fmtc[i];
                }
            }
            if (max > 0) { // one of the formats matched
                CSV.dateformat[k] = fmts[maxIndex];
            }
        } // date found
    }
    //alert(JSON.stringify(CSV.dateformat));
}

function reprocessCsv() {
    spinit.spin(document.getElementById('divSpinner'));
    _.delay(parseAndSort, 300);
}

function showGrid() {
    var availableWidth = null;
    var availableHeight = null;

    var calculateSize = function () {
        availableWidth = 1200;
        availableHeight = 1000;
    };

    const dataTable = $("#dataTable");
    dataTable.handsontable({
        data: (document.getElementById('txt1').value === "") ? [[]] : CSV.table || [],
        startRows: 15,
        startCols: (1 > CSV.maxColumnsFound) ? CSV.maxColumnsFound : 1,
        minCols: 1,
        minRows: 1,
        manualColumnResize: true,
        manualColumnMove: true,
        columnSorting: true,
        cells: function (row, col, prop) {
            if (!CSV.statsCnt) return;
            if (col >= CSV.statsCnt.length) return;
            if (CSV.statsCnt[col].fieldType === "I" || CSV.statsCnt[col].fieldType === "N") {
                this.type = "numeric";
                if (CSV.statsCnt[col].fieldDecs > 0) this.format = "0." + "0".repeat(CSV.statsCnt[col].fieldDecs);
            }
        },
        filters: true,
        dropdownMenu: ['filter_by_condition', 'filter_action_bar'],
        colHeaders: (document.getElementById('txt1').value === "" || !CSV.isFirstRowHeader) ? [] : CSV.arHeaderRow,
        rowHeaders: true,
        contextMenu: true,
        scrollH: 'auto',
        scrollV: 'auto',
        stretchH: 'all',
        minSpareRows: 1,
        minSpareCols: 1,
        autoWrapRow: false,
        width: function () {
            if (!availableWidth) {
                calculateSize();
            }
            return availableWidth || 800;
        },
        height: function () {
            if (!availableHeight) calculateSize();
            return availableHeight || 600;
        }
    });
    var cols = [];
    var setit = false;
    if (CSV.table.length > 0) {
        for (j = 0; j < CSV.table[0].length; j++) {
            cols.push({});
            if (j < CSV.statsCnt.length && (CSV.statsCnt[j].fieldType === "N" || CSV.statsCnt[j].fieldType === "I")) {
                cols[j].type = "numeric";
                setit = true;
            }
        }
        if (setit) {
            dataTable.handsontable("updateSettings", {columns: cols});
        }
    }
    dataTable.handsontable("selectCell", 0, 0);
    dataTable.handsontable("render");
}

function ieReadLocalFile(that) {
    if(!that.value)return;
    if(that.value.length<=0)return;
    var request;
    if (window.XMLHttpRequest && false) {
        request=new XMLHttpRequest();
    }else {
        request=new ActiveXObject("Msxml2.XMLHTTP");
    }
    var fn=that.value;
    request.open('get', fn, true);
    request.onreadystatechange = function() {
        if (request.readyState === 4 && (request.status === 200 || request.status===0)) {
            assignText(request.responseText);
        }
    };
    request.send();
}

function readLocalFile(that) {
    if(that.files && that.files[0]){
        var reader = new FileReader();
        reader.onload = function (e) {
            assignText(e.target.result);
        };
        reader.readAsText(that.files[0]);
    }
}

function loadTextFile(f) {
    if(window.FileReader){
        readLocalFile(f)
    }else{
        ieReadLocalFile(f)
    }
}

const spinit = {
    opts : {
        lines: 13, // The number of lines to draw
        length: 20, // The length of each line
        width: 10, // The line thickness
        radius: 30, // The radius of the inner circle
        corners: 1, // Corner roundness (0..1)
        rotate: 0, // The rotation offset
        direction: 1, // 1: clockwise, -1: counterclockwise
        color: '#000', // #rgb or #rrggbb or array of colors
        speed: 1, // Rounds per second
        trail: 60, // Afterglow percentage
        shadow: false, // Whether to render a shadow
        hwaccel: false, // Whether to use hardware acceleration
        className: 'spinner', // The CSS class to assign to the spinner
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        top: 'auto', // Top position relative to parent in px
        left: 'auto' // Left position relative to parent in px
    },
    spinner : null,
    spin : function (targetObj) {
        if(!spinit.spinner) spinit.spinner = new Spinner(spinit.opts);
        spinit.spinner.spin(targetObj);
    },
    stop: function () {
        spinit.spinner.stop();
    }
};