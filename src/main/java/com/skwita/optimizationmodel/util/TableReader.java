package com.skwita.optimizationmodel.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class TableReader {
    private MultipartFile multipartFile;

    public TableReader(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public List<List<Double>> readAll() {
        return readColumns(new int[]{31,36});
    }

    public List<List<Double>> readParetoFull() {
        return readParetoColumns(new int[]{27,28,29,30,31,36}, 4, 5);
    }

    private List<List<Double>> readParetoColumns(int[] columns, int timeColumn, int costColumn) {
        List<List<Double>> allPoints = readColumns(columns)
                                        .stream()
                                        .sorted((a, b) -> a.get(timeColumn).compareTo(b.get(timeColumn)))
                                        .toList();

        List<List<Double>> result = new ArrayList<>();
        result.add(allPoints.get(0));
        for (int i = 0; i < allPoints.size(); i++) {
            if (result.get(result.size() - 1).get(costColumn) > allPoints.get(i).get(costColumn)) {
                result.add(allPoints.get(i));
            }    
        }
        return result;
    }

    private List<List<Double>> readColumns (int[] columns) {
        List<List<Double>> result = new ArrayList<>();

        try (InputStream fileInputStream = multipartFile.getInputStream()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {

                XSSFSheet sheet = workbook.getSheetAt(1);
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    Cell[] cells = new Cell[columns.length];

                    for (int i = 0; i < columns.length; i++) {
                        if (row.getCell(columns[i]) == null) return new ArrayList<>();
                        cells[i] = row.getCell(columns[i]);
                    }

                    List<Double> tempDoubles = new ArrayList<>();

                    for (Cell cell : cells) {
                        tempDoubles.add(getCellValueAsDouble(cell, evaluator));
                    }

                    result.add(tempDoubles);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Double getCellValueAsDouble(Cell cell, FormulaEvaluator evaluator) {
        if (cell.getCellType().equals(CellType.NUMERIC)) {
            return cell.getNumericCellValue();
        }
        if (cell.getCellType().equals(CellType.FORMULA)) {
            CellValue cellValue = evaluator.evaluate(cell);

            if (cellValue.getCellType().equals(CellType.NUMERIC)) {
                return cellValue.getNumberValue();
            }
        }
        return null;
    }

}
