function drawChartAll(stepAll100, elementId) {
    google.charts.load("current", { packages: ["corechart"] });
    google.charts.setOnLoadCallback(function () {
        var header = ["Срок", "заработная плата"];
        var data = stepAll100;
        var result = []
        result.push(header);
        data.forEach(function (point) {
            result.push([point[2], point[1]]);
        });
        var dataAll = google.visualization.arrayToDataTable(result);

        var options = {
            explorer: {},
            title: "Сравнение срока и заработной платы",
            pointSize: 3,
            hAxis: { title: "Срок", minValue: 0, maxValue: 15 },
            vAxis: { title: "ЗП", minValue: 0, maxValue: 15 },
            legend: "none",
        };

        var chart = new google.visualization.ScatterChart(
            document.getElementById(elementId)
        );

        chart.draw(dataAll, options);
    });
}