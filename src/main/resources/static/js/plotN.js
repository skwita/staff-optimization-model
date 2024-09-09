function drawChartN(stepAllN, stepParetoN, chartElementId, numIterations) {
    google.charts.load('current', {'packages':['corechart']});
    google.charts.setOnLoadCallback(function() {
        var data = new google.visualization.DataTable();
        data.addColumn('number', 'Срок проекта (дни)');
        data.addColumn('number', 'ЗП с коэффициентами на привлечение людей');
        data.addColumn({type: 'string', role: 'style'});
        data.addColumn({type: 'string', role: 'annotation'});
        data.addColumn({type: 'string', role: 'annotationText', p: { html: true } });

        var bluePointsArray = stepAllN;
        var redPointsArray = stepParetoN;

        // Add blue points to the data table
        bluePointsArray.forEach(function(point) {
            data.addRow([point[5], point[4], 'point { size: 3; fill-color: blue; }', null, null]);
        });

        // Add red points with annotations to the data table
        redPointsArray.forEach(function(point) {
            var annotation = '' + point[0];
            data.addRow([point[5], point[4], 'point { size: 3; fill-color: red; }', annotation, annotation]);
        });

        var options = {
            explorer: {},
            title: 'Сравнение срока и заработной платы ' + numIterations + '%',
            hAxis: {title: 'Срок проекта (дни)'},
            vAxis: {title: 'ЗП с коэффициентами на привлечение людей'},
            annotations: {
                textStyle: {
                    fontSize: 12,
                    auraColor: "none",
                    color: "#555",
                },
                boxStyle: {
                    stroke: "#888",
                    strokeWidth: 1,
                    color: "#ffffff"
                }
                },
            pointSize: 5,
            lineWidth: 0,
            legend: "none",
        };

        var chart = new google.visualization.ScatterChart(document.getElementById(chartElementId));

        chart.draw(data, options);
    });
}