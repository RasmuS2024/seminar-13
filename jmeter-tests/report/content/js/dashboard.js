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

    var data = {"OkPercent": 99.93913572732806, "KoPercent": 0.06086427267194157};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.02878880097382836, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.018821198613174838, 500, 1500, "Update_satellite-35"], "isController": false}, {"data": [0.03011764705882353, 500, 1500, "Add_satellite-21"], "isController": false}, {"data": [0.018821198613174838, 500, 1500, "Update_satellite"], "isController": true}, {"data": [0.04874746106973595, 500, 1500, "Get_overview"], "isController": true}, {"data": [0.014281864726488817, 500, 1500, "Delete_satellite-37"], "isController": false}, {"data": [0.04874746106973595, 500, 1500, "Get_overview-20"], "isController": false}, {"data": [0.014281864726488817, 500, 1500, "Delete_satellite"], "isController": true}, {"data": [0.03011764705882353, 500, 1500, "Add_satellite"], "isController": true}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 16430, 10, 0.06086427267194157, 4026.7250760803354, 29, 12467, 3879.0, 5820.9, 6343.0, 7700.760000000002, 217.51217962296124, 112.64170602427981, 117.02606259349847], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Update_satellite-35", 4038, 3, 0.07429420505200594, 4086.9229816741013, 29, 10453, 3879.0, 5820.4, 6273.399999999998, 7593.0300000000025, 54.10257784447184, 24.25514015522, 32.591458461734284], "isController": false}, {"data": ["Add_satellite-21", 4250, 4, 0.09411764705882353, 4141.752470588243, 52, 11222, 4026.0, 5976.9, 6491.249999999999, 7919.449999999999, 56.4611480876277, 25.314184493443864, 34.52498011405152], "isController": false}, {"data": ["Update_satellite", 4038, 3, 0.07429420505200594, 4086.923229321446, 29, 10453, 3879.0, 5820.4, 6273.399999999998, 7593.0300000000025, 54.10257784447184, 24.25514015522, 32.591458461734284], "isController": true}, {"data": ["Get_overview", 4431, 0, 0.0, 3818.943579327467, 31, 12467, 3801.0, 5581.6, 6142.399999999992, 7470.760000000002, 58.539098727755544, 58.424764550552894, 24.12451138975863], "isController": true}, {"data": ["Delete_satellite-37", 3711, 3, 0.08084074373484236, 4077.583400700623, 110, 11574, 3856.0, 5885.8, 6419.4, 7739.560000000001, 49.7746660228553, 4.967768564904233, 26.588458360997105], "isController": false}, {"data": ["Get_overview-20", 4431, 0, 0.0, 3818.9433536447777, 31, 12467, 3801.0, 5581.6, 6142.399999999992, 7470.760000000002, 58.723742628056456, 58.609047818236036, 24.200604872109206], "isController": false}, {"data": ["Delete_satellite", 3711, 3, 0.08084074373484236, 4077.583400700622, 110, 11574, 3856.0, 5885.8, 6419.4, 7739.560000000001, 49.77399841731829, 4.967701934426009, 26.58810174178816], "isController": true}, {"data": ["Add_satellite", 4250, 4, 0.09411764705882353, 4141.752705882357, 52, 11222, 4026.0, 5976.9, 6491.249999999999, 7919.449999999999, 56.45964795748921, 25.313511914646295, 34.52406281135835], "isController": true}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["404", 6, 60.0, 0.036518563603164945], "isController": false}, {"data": ["409", 4, 40.0, 0.024345709068776627], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 16430, 10, "404", 6, "409", 4, "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["Update_satellite-35", 4038, 3, "404", 3, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["Add_satellite-21", 4250, 4, "409", 4, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["Delete_satellite-37", 3711, 3, "404", 3, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
