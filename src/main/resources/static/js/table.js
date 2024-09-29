function drawTable(stepPareto100, elementId) {
    google.charts.load("current", { packages: ['table'] });
    google.charts.setOnLoadCallback(function () {
        var data = new google.visualization.DataTable();
        data.addColumn("number", "№");
        data.addColumn("number", "ЗП");
        data.addColumn("number", "Срок");
        data.addColumn("number", "Кол-во аналитиков");
        data.addColumn("number", "Кол-во разработчиков");
        data.addColumn("number", "Кол-во тестировщиков");

        // Example data array
        var dataArray = stepPareto100;

        dataArray.forEach(function (row) {
            data.addRow(row);
        });

        data.sort([{ column: 0 }]);

        var table = new google.visualization.Table(
            document.getElementById(elementId)
        );

        table.draw(data, {
            showRowNumber: false,
            //width: "100%",
            height: "100%",
        });
    });
}