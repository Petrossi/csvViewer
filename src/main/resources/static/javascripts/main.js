

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

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, '\\$&');
    let regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'), results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}