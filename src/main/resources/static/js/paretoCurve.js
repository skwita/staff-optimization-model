function drawParetoCurve(stepPareto100, elementId) {
    google.charts.load("current", { packages: ["corechart"] });
    google.charts.setOnLoadCallback(function () {
        var data = new google.visualization.DataTable();
        data.addColumn("number", "Срок проекта (дни)");
        data.addColumn("number", "ЗП с коэффициентами на привлечение людей");
        data.addColumn({ type: "string", role: "annotation" });
        data.addColumn({ type: "string", role: "annotationText" });

        // Example data array
        var dataArray = stepPareto100;

        dataArray.forEach(function (row) {
            var projectDuration = row[2];
            var salary = row[1];
            var operationNumber = "" + row[0];
            data.addRow([
                projectDuration,
                salary,
                operationNumber,
                operationNumber,
            ]);
        });

        var options = {
            explorer: {},
            title: "Сравнение срока и заработной платы",
            hAxis: { title: "Срок проекта (дни)" },
            vAxis: { title: "ЗП с коэффициентами на привлечение людей" },
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
                },
            },
            curveType: "function",
            pointSize: 5,
            lineWidth: 2,
            legend: "none",
        };

        var chart = new google.visualization.LineChart(
            document.getElementById(elementId)
        );

        chart.draw(data, options);
    });
}