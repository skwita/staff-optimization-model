function drawAreaPlot(avgArea, elementId) {
    google.charts.load("current", { packages: ['corechart'] });
    google.charts.setOnLoadCallback(function () {
        var data = google.visualization.arrayToDataTable([
            ["Element", "Area"],
            ["10%", avgArea[0]],
            ["20%", avgArea[1]],
            ["30%", avgArea[2]],
            ["40%", avgArea[3]],
            ["50%", avgArea[4]],
            ["60%", avgArea[5]],
            ["70%", avgArea[6]],
            ["80%", avgArea[7]],
            ["90%", avgArea[8]],
            ["100%", avgArea[9]],
        ]);

        var view = new google.visualization.DataView(data);
        view.setColumns([0, 1,
            {
                calc: "stringify",
                sourceColumn: 1,
                type: "string",
                role: "annotation"
            }]);

        var options = {
            title: "Площадь под графиком при 1000 прогонах",
            width: "100%",
            height: 400,
            bar: { groupWidth: "95%" },
            legend: { position: "none" },
        };
        var chart = new google.visualization.LineChart(document.getElementById(elementId));
        chart.draw(view, options);
    });
}