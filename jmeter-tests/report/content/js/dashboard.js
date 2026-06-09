/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 99.84236911468292, "KoPercent": 0.15763088531707065};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.024226058194221925, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.014853049615506162, 500, 1500, "Update_satellite-35"], "isController": false}, {"data": [0.025176233635448138, 500, 1500, "Add_satellite-21"], "isController": false}, {"data": [0.014853049615506162, 500, 1500, "Update_satellite"], "isController": true}, {"data": [0.042926122646064706, 500, 1500, "Get_overview"], "isController": true}, {"data": [0.011434977578475336, 500, 1500, "Delete_satellite-37"], "isController": false}, {"data": [0.042926122646064706, 500, 1500, "Get_overview-20"], "isController": false}, {"data": [0.011434977578475336, 500, 1500, "Delete_satellite"], "isController": true}, {"data": [0.025176233635448138, 500, 1500, "Add_satellite"], "isController": true}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 38698, 61, 0.15763088531707065, 3407.70109566385, 16, 12467, 2884.5, 4071.800000000003, 4545.0, 5465.990000000002, 12.881638256248667, 6.6547394655957595, 7.055529733676672], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Update_satellite-35", 9493, 20, 0.21068155482987463, 3482.158959233101, 26, 10453, 3292.0, 5097.0, 5706.299999999999, 6991.299999999997, 3.160903752761587, 1.4358824971780617, 1.9876451628108502], "isController": false}, {"data": ["Add_satellite-21", 9930, 22, 0.2215508559919436, 3486.770392749243, 24, 11222, 3330.0, 5172.9, 5884.449999999999, 7233.0, 3.305747706533496, 1.4819033486383681, 2.0620337351886358], "isController": false}, {"data": ["Update_satellite", 9493, 20, 0.21068155482987463, 3482.159380596234, 26, 10453, 3292.0, 5097.0, 5706.299999999999, 6991.299999999997, 3.160903752761587, 1.4358824971780617, 1.9876451628108502], "isController": true}, {"data": ["Get_overview", 10355, 0, 0.0, 3246.9645581844466, 16, 12467, 3115.0, 4839.0, 5514.199999999999, 6925.4400000000005, 3.4466655549464393, 3.4399337862844344, 1.4204031876830052], "isController": true}, {"data": ["Delete_satellite-37", 8920, 19, 0.21300448430493274, 3427.0330717488723, 24, 11574, 3206.0, 5065.700000000002, 5739.849999999997, 7012.949999999995, 2.970223509319076, 0.2973943572746002, 1.5866083523051364], "isController": false}, {"data": ["Get_overview-20", 10355, 0, 0.0, 3246.964461612759, 16, 12467, 3115.0, 4839.0, 5514.199999999999, 6925.4400000000005, 3.446938615865238, 3.4402063138811267, 1.4205157186475885], "isController": false}, {"data": ["Delete_satellite", 8920, 19, 0.21300448430493274, 3427.0330717488728, 24, 11574, 3206.0, 5065.700000000002, 5739.849999999997, 7012.949999999995, 2.970222520280426, 0.29739425824686283, 1.586607823989009], "isController": true}, {"data": ["Add_satellite", 9930, 22, 0.2215508559919436, 3486.7704934541753, 24, 11222, 3330.0, 5172.9, 5884.449999999999, 7233.0, 3.305745505534377, 1.4819023619726412, 2.06203236226643], "isController": true}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["404", 39, 63.9344262295082, 0.1007804020879632], "isController": false}, {"data": ["409", 22, 36.0655737704918, 0.05685048322910745], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 38698, 61, "404", 39, "409", 22, "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["Update_satellite-35", 9493, 20, "404", 20, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["Add_satellite-21", 9930, 22, "409", 22, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["Delete_satellite-37", 8920, 19, "404", 19, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
