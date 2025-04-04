function drawTable(stepPareto100, elementId) {
    google.charts.load("current", { packages: ['table'] });
    google.charts.setOnLoadCallback(function () {
        var data = new google.visualization.DataTable();
        data.addColumn("number", "№");
        data.addColumn("number", "ЗП");
        data.addColumn("number", "Срок");
        data.addColumn("number", "J-Аналитики");
        data.addColumn("number", "M-Аналитики");
        data.addColumn("number", "S-Аналитики");
        data.addColumn("number", "J-Разработчики");
        data.addColumn("number", "M-Разработчики");
        data.addColumn("number", "S-Разработчики");
        data.addColumn("number", "J-Тестировщики");
        data.addColumn("number", "M-Тестировщики");
        data.addColumn("number", "S-Тестировщики");

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