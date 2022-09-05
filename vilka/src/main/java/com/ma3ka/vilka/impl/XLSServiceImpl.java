package com.ma3ka.vilka.impl;

import com.ma3ka.vilka.config.FilesConfig;
import com.ma3ka.vilka.domain.AggregatedCarData;
import com.ma3ka.vilka.domain.CarDetailed;
import com.ma3ka.vilka.domain.MachineCoreInfo;
import com.ma3ka.vilka.repository.AggregatedDataRepository;
import com.ma3ka.vilka.repository.CarDetailedRepository;
import com.ma3ka.vilka.repository.MarkRepository;
import com.ma3ka.vilka.service.XLSService;
import com.ma3ka.vilka.util.OffsetBasedPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class XLSServiceImpl implements XLSService {

    private FilesConfig filesConfig;

    @Autowired
    private CarDetailedRepository carDetailedRepository;

    @Autowired
    private AggregatedDataRepository aggregatedDataRepository;

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    public XLSServiceImpl(FilesConfig filesConfig) {
        this.filesConfig = filesConfig;
    }

    private static int rowIndex = 0;

    @Override
    public void parse() {
        // Read XSL file
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("C:\\autostat\\vilka\\example\\huge.xls");
        } catch (FileNotFoundException e) {
            log.info(e.getMessage());
        }

        // Get the workbook instance for XLS file
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        // Get first sheet from the workbook
        HSSFSheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();

        List<CarDetailed> carDetailedList = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            // Get iterator to all cells of current row
            Iterator<Cell> cellIterator = row.cellIterator();

            StringBuilder wholeCarInfo = new StringBuilder();
            CarDetailed carDetailed = null;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                // Change to getCellType() if using POI 4.x
                CellType cellType = cell.getCellTypeEnum();

                switch (cellType) {
                    case FORMULA:
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        // Print out value evaluated by formula
                        wholeCarInfo.append(" " + evaluator.evaluate(cell).getNumberValue());
                        break;
                    case NUMERIC:
                        wholeCarInfo.append(" " + cell.getNumericCellValue());
                        break;
                    case STRING:
                        wholeCarInfo.append(" " + cell.getStringCellValue());
                        break;
                    case ERROR:
                        System.out.print("ERROR!");
                        break;
                    default:
                        System.out.println("Something strange " + cell.getRowIndex());
                        break;
                }
            }
            carDetailed = new CarDetailed(wholeCarInfo.toString().replaceAll("[^\\da-zA-Zа-яёА-ЯЁ ]", ""));
            carDetailedList.add(carDetailed);
            if (carDetailedList.size() == 100) {
                carDetailedRepository.saveAll(carDetailedList);
                carDetailedList.clear();
            }
        }
        System.out.println("Finished");
    }

    @Override
    public void save() {
        long limit = carDetailedRepository.count();
        int currentOffset = 0;
        List<AggregatedCarData> aggregatedCarDataList = new ArrayList<>();
        while (currentOffset <= limit) {
            Pageable pageable = new OffsetBasedPageRequest(currentOffset, 100);
            List<CarDetailed> carDetailedList = carDetailedRepository.findAllCars(pageable);
            currentOffset += 100;
            aggregatedCarDataList = carDetailedList
                    .stream().map(car -> getAggragatedCarData(car.getFullDetail(), getMachineInfo(car.getFullDetail()))).filter(Objects::nonNull).collect(Collectors.toList());
            aggregatedDataRepository.saveAll(aggregatedCarDataList);
            aggregatedCarDataList.clear();
        }

        try {
            safeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadUniqueMarkModels() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("C:\\autostat\\vilka\\example\\unique.xls");
        } catch (FileNotFoundException e) {
            log.info(e.getMessage());
        }

        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        HSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        Map<String, List<String>> carData = new HashMap<>();
        List<MachineCoreInfo> machineCoreInfos = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String cellMark = row.getCell(0).getStringCellValue();
            String cellModel = row.getCell(1).getStringCellValue();
            //    List<String> carMark = carData.get(cellMark);
//            if (carMark!=null){
//                carMark.add(cellModel);
//            }
//            else{
//                carData.put(cellMark, singletonList(cellModel));
//            }
            MachineCoreInfo machineCoreInfo = new MachineCoreInfo();
            machineCoreInfo.setMark(cellMark);
            machineCoreInfo.setModel(cellModel);
            machineCoreInfos.add(machineCoreInfo);
            if (machineCoreInfos.size() == 100) {
                markRepository.saveAll(machineCoreInfos);
                machineCoreInfos.clear();
            }
        }

        System.out.println("Finished loading documents");
    }

    private AggregatedCarData getAggragatedCarData(String carFullData, MachineCoreInfo machineCoreInfo) {
        if (machineCoreInfo == null) {
            System.out.println("Cant parse " + carFullData);
            return null;
        }

        AggregatedCarData aggregatedCarData = new AggregatedCarData();
        aggregatedCarData.setDetails(carFullData);
        aggregatedCarData.setMark(machineCoreInfo.getMark());
        aggregatedCarData.setModel(machineCoreInfo.getModel());
        aggregatedCarData.setNumber("some");
        return aggregatedCarData;
    }

    private MachineCoreInfo getMachineInfo(String fullInfoCar) {

        Query searchQuery = new StringQuery(
                "{\"multi_match\":{\"query\":\"" + fullInfoCar + "\", \"fields\": [\"mark\", \"model\"], \"operator\": \"or\"}}}\"");

        SearchHits<MachineCoreInfo> products = elasticsearchTemplate.search(
                searchQuery,
                MachineCoreInfo.class,
                IndexCoordinates.of("vilka"));

        return products.getSearchHits().stream()
                .map(SearchHit::getContent)
                .filter(result->fullInfoCar.contains(result.getMark()))
             //   .filter(result->fullInfoCar.contains(result.getModel()))
                .findFirst()
                .orElse(null);
    }

    private void createCell(XSSFRow row) {
        XSSFCell cell = row.createCell(0);
        cell.setCellType(CellType.STRING);

        cell = row.createCell(1);
        cell.setCellType(CellType.STRING);

        cell = row.createCell(2);
        cell.setCellType(CellType.STRING);

        cell = row.createCell(3);
        cell.setCellType(CellType.STRING);

    }

    private void savePartData(XSSFSheet sheet, AggregatedCarData aggregatedCarData) {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell cell = row.createCell(0);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(aggregatedCarData.getNumber());

        cell = row.createCell(1);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(aggregatedCarData.getMark());

        cell = row.createCell(2);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(aggregatedCarData.getModel());

        cell = row.createCell(3);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(aggregatedCarData.getDetails());

        rowIndex += 1;
    }

    private void safeToFile() throws IOException {
        XSSFWorkbook book = new XSSFWorkbook();
        FileOutputStream fileOut = new FileOutputStream("C:\\autostat\\vilka\\example\\output.xlsx");

        XSSFSheet sheet = book.createSheet("Sheet 1");
        XSSFRow row = sheet.createRow((short) 0);
        row.setHeightInPoints(80.0f);
        createCell(row);
        long limit = aggregatedDataRepository.count();
        int currentOffset = 0;

        while (currentOffset <= limit) {
            Pageable pageable = new OffsetBasedPageRequest(currentOffset, 100);
            Iterable<AggregatedCarData> carDetailedList = aggregatedDataRepository.findAll(pageable);
            currentOffset += 100;

            carDetailedList.forEach(carData -> savePartData(sheet, carData));
        }
        book.write(fileOut);
        fileOut.close();
    }
}
