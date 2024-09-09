function drawGraph(graphJson, graphElementId) {
    const data = graphJson;
    const elements = [];

    data.forEach((task) => {
        elements.push({ data: { id: task.code, label: task.code } });
    });

    data.forEach((task) => {
        task.predecessors.forEach((predecessor) => {
            elements.push({ data: { source: predecessor, target: task.code } });
        });
    });

    const cy = cytoscape({
        container: document.getElementById(graphElementId),
        elements: elements,
        style: [
            {
                selector: "node",
                style: {
                    "background-color": "#666",
                    label: "data(label)",
                    "text-valign": "center",
                    color: "#fff",
                    "text-outline-width": 2,
                    "text-outline-color": "#888",
                },
            },
            {
                selector: "edge",
                style: {
                    width: 2,
                    "line-color": "#ccc",
                    "target-arrow-color": "#ccc",
                    "target-arrow-shape": "triangle",
                },
            },
        ],
        layout: {
            name: "dagre",
            rankDir: "LR",
        },
    });
}