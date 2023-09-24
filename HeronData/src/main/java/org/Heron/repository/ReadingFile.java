package org.Heron.repository;


import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Label;


import javafx.scene.layout.VBox;
import org.Heron.Utils.ExportUtils;
import org.Heron.models.MassSpecData;


import javax.swing.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

import javafx.scene.chart.NumberAxis;
import javafx.embed.swing.JFXPanel;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import static java.awt.BorderLayout.CENTER;


public class ReadingFile {


    public static void readFile(String text) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(text))) {



            List<MassSpecData> dataList = new ArrayList<>();


            Map<String, List<Float>> peptideValuesByName = new LinkedHashMap<>();


            //BufferedReader br = new BufferedReader(new FileReader(text));

            String line = "";
            String splitBy = ",";



            int iteration = 0;
            int batchNum = 3;
            Integer groupNumber = 0;

            Map<String, Float> ratioToStandardBatch = new LinkedHashMap<>();
            Map<String, Float> quantificationBatch = new LinkedHashMap<>();

            List<Float> temporaryListForRatioAvgCalculation = new ArrayList<>();
            List<Float> temporaryListForQuantificationCalculation = new ArrayList<>();

            JTable table = new JTable();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            JTable avgTable = new JTable() {
                @Override
                public  boolean getScrollableTracksViewportHeight() {
                    return getPreferredSize().height < getParent().getHeight();
                }
                @Override
                public  boolean getScrollableTracksViewportWidth() {
                    return getPreferredSize().width < getParent().getWidth();
                }
            };
            avgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            DefaultTableModel avgTableModel = (DefaultTableModel) avgTable.getModel();



            while ((line = br.readLine()) != null) {
                String[] replicate = line.split(splitBy);
                if (iteration == 0) {
                    model.addColumn("Group ID for Technical Replicate");
                    model.addColumn(replicate[0]);
                    model.addColumn(replicate[1]);
                    model.addColumn(replicate[2]);
                    model.addColumn(replicate[3]);
                    model.addColumn("Ratio Avg");
                    model.addColumn(replicate[4]);
                    model.addColumn(replicate[5]);
                    model.addColumn("Quant Avg");
                    model.addColumn(replicate[6]);

                    avgTableModel.addColumn("Group ID for Technical Replicate");
                    avgTableModel.addColumn("Replicate Name");
                    avgTableModel.addColumn("Quant Avg");
                    avgTableModel.addColumn("Ratio Avg");



                    iteration++;
                    continue;
                }



                    peptideValuesByName.computeIfAbsent(replicate[0], k -> new ArrayList<>());



                MassSpecData massSpecData = new MassSpecData();
                massSpecData.setPeptide(replicate[0]);
                massSpecData.setProtein(replicate[1]);
                massSpecData.setReplicateName(replicate[2]);
                massSpecData.setRatioToStandard(parsingFloatValues(replicate[3]));
                massSpecData.setPepRetentionTime(parsingDoubleValues(replicate[4]));
                massSpecData.setQuantificationWithConcentration(replicate[5]);
                massSpecData.setQuantification(parsingFloatValues(replicate[5]));
                massSpecData.setPepPeakFoundRatio(parsingDoubleValues(replicate[6]));

                dataList.add(massSpecData);


                temporaryListForRatioAvgCalculation.add(massSpecData.getRatioToStandard());
                temporaryListForQuantificationCalculation.add(massSpecData.getQuantification());


                iteration++;

                if (temporaryListForRatioAvgCalculation.size() == batchNum && temporaryListForQuantificationCalculation.size() == batchNum) {


                    Float ratioToStandardBatchSum = temporaryListForRatioAvgCalculation.stream().reduce(0.0f, Float::sum);
                    Float quantBatchSum = temporaryListForQuantificationCalculation.stream().reduce(0.0f, Float::sum);

                    Float ratioToStandardBatchAvg = ratioToStandardBatchSum / temporaryListForRatioAvgCalculation.size();
                    Float quantBatchAvg = quantBatchSum / temporaryListForQuantificationCalculation.size();


                    groupNumber++;


                    ratioToStandardBatch.put("Ratio Avg Group_".concat(groupNumber.toString()), ratioToStandardBatchAvg);
                    quantificationBatch.put("Quantification Avg Group_".concat(groupNumber.toString()), quantBatchAvg);

                    temporaryListForRatioAvgCalculation.clear();
                    temporaryListForQuantificationCalculation.clear();
                }





            }



            Integer groupAvgCounter = 0;

            for (int i = 0; i < dataList.size(); i++) {



                model.addRow(new Object[0]);

                model.setValueAt(dataList.get(i).getPeptide(), i, 1);
                model.setValueAt(dataList.get(i).getProtein(), i, 2);
                model.setValueAt(dataList.get(i).getReplicateName(), i, 3);
                model.setValueAt(dataList.get(i).getRatioToStandard(), i, 4);

                model.setValueAt(dataList.get(i).getPepRetentionTime(), i, 6);
                model.setValueAt(dataList.get(i).getQuantificationWithConcentration(), i, 7);

                model.setValueAt(dataList.get(i).getPepPeakFoundRatio(), i, 9);


                if ((i + 1) % batchNum == 0) {
                    groupAvgCounter++;

                    Float ratioBatchAvg = ratioToStandardBatch.get("Ratio Avg Group_".concat(groupAvgCounter.toString()));
                    Float quantBatchAvg = quantificationBatch.get("Quantification Avg Group_".concat(groupAvgCounter.toString()));

                    model.setValueAt(ratioBatchAvg, i, 5);
                    model.setValueAt(quantBatchAvg, i, 8);

                    model.setValueAt("Group_".concat(groupAvgCounter.toString()), i, 0);

                    peptideValuesByName.get(dataList.get(i).getPeptide()).add(ratioBatchAvg);
                    peptideValuesByName.get(dataList.get(i).getPeptide()).add(quantBatchAvg);



                    avgTableModel.addRow(new Object[0]);
                    avgTableModel.setValueAt("Group_".concat(groupAvgCounter.toString()), (groupAvgCounter-1), 0);
                    avgTableModel.setValueAt(dataList.get(i).getReplicateName(), (groupAvgCounter-1), 1);
                    avgTableModel.setValueAt(quantBatchAvg, (groupAvgCounter-1), 2);
                    avgTableModel.setValueAt(ratioBatchAvg, (groupAvgCounter-1), 3);

                }


            }





            int cols = (peptideValuesByName.size() + 1) % 2 == 0 ?  (peptideValuesByName.size() + 1) / 2 : 2;
            int rows = cols % 2 == 0 ? cols : cols - 1;


           JPanel mainPanel = new JPanel(new GridLayout(rows, cols, 10, 10));

            mainPanel.add(new JScrollPane(avgTable));


            List<JFXPanel> panels = new ArrayList<>();
            for (int i = 0; i < peptideValuesByName.size(); i++) {

                panels.add(new JFXPanel());



                mainPanel.add(panels.get(i));



            }


            JFrame jf = new JFrame();

            jf.setSize(Toolkit.getDefaultToolkit().getScreenSize());

            jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);



            JButton csvButton = new JButton("Export to CSV");
            JButton pdfButton = new JButton("Export to PDF");

            csvButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV FILES", "csv", ".csv");
                    chooser.setFileFilter(filter);

                    int r = chooser.showSaveDialog(null);

                    if (r == JFileChooser.APPROVE_OPTION) {

                        File file = chooser.getSelectedFile();
                        if (file.exists()) {
                            int reply = JOptionPane.showConfirmDialog(jf, "Do you wish to overwrite existing file?", "File Exists!", JOptionPane.YES_NO_OPTION);

                            if (reply == JOptionPane.YES_OPTION) {
                                if (!file.getName().contains(".csv")) {
                                    String oldName = file.getAbsolutePath();
                                    file = new File(oldName.concat(".csv"));
                                }
                                ExportUtils.exportToCSV(table, avgTable, file);

                            } else {

                                JOptionPane.showMessageDialog(jf, "File not saved");
                            }
                        } else {

                            if (!file.getName().contains(".csv")) {
                                String oldName = file.getAbsolutePath();
                                file = new File(oldName.concat(".csv"));
                            }
                            ExportUtils.exportToCSV(table, avgTable, file);
                        }


                    }
                }
            });

            pdfButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                    JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Only", "pdf", ".pdf");
                    chooser.setFileFilter(filter);


                    int r = chooser.showSaveDialog(jf);

                    if (r == JFileChooser.APPROVE_OPTION) {

                        File file = chooser.getSelectedFile();
                        if (file.exists()) {
                            int reply = JOptionPane.showConfirmDialog(jf, "Do you wish to overwrite existing file?", "File Exists!", JOptionPane.YES_NO_OPTION);

                            if (reply == JOptionPane.YES_OPTION) {
                                if (!file.getName().contains(".pdf")) {
                                    String oldName = file.getAbsolutePath();
                                    file = new File(oldName.concat(".pdf"));
                                }

                                ExportUtils.exportToPdf(mainPanel, file);

                            } else {

                                JOptionPane.showMessageDialog(jf, "File not saved");
                            }
                        } else {

                            if (!file.getName().contains(".pdf")) {
                                String oldName = file.getAbsolutePath();
                                file = new File(oldName.concat(".pdf"));
                            }
                            ExportUtils.exportToPdf(mainPanel, file);
                        }

                    }

                }
            });



            JPanel buttonpan = new JPanel();
            buttonpan.add(csvButton);
            buttonpan.add(pdfButton);

           jf.setLayout(new BorderLayout());
           jf.add(new JScrollPane(table), BorderLayout.NORTH);
           jf.add(mainPanel, BorderLayout.CENTER);
           jf.add(buttonpan, BorderLayout.PAGE_END);


            jf.setVisible(true);


            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    initFX(panels, peptideValuesByName);
                }
            });


        } catch (
                IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "File Read Error", JOptionPane.ERROR_MESSAGE);

        }


    }

    private static void initFX(List<JFXPanel> fxPanel, Map<String, List<Float>> peptides) {
        // This method is invoked on the JavaFX thread
        List<Scene> scenes = createScene(peptides);
        for (int i =0; i< scenes.size(); i++) {

            fxPanel.get(i).setScene(scenes.get(i));


        }

    }
    private static List<Scene> createScene(Map<String, List<Float>> peptides) {


        List<Scene> scenes = new ArrayList<>();
       // try {

            for (Map.Entry<String, List<Float>> entry : peptides.entrySet() ) {

                SimpleRegression regression = new SimpleRegression();

                NumberAxis xAxis = new NumberAxis();
                xAxis.setLabel("Analyte Concentration (fmol)");

                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Light:Heavy Peak Area Ratio");

                final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);


                XYChart.Series series = new XYChart.Series<>();

                series.setName("Ratio Average Trendline");

                List<Float> f = entry.getValue();
                for (int i = 0; i < f.size()-1; i++) {


                    //swapped the i+1's here
                    series.getData().add(new XYChart.Data<>(entry.getValue().get(i+1), entry.getValue().get(i)));


                    regression.addData(entry.getValue().get(i+1).doubleValue(), entry.getValue().get(i).doubleValue() );

                    i++;
                }



                lineChart.setTitle(entry.getKey());
                lineChart.getData().add(series);
                lineChart.setId(Double.toString(regression.getRSquare()));




                Label rSquare = new Label("R-Squared: " + regression.getRSquare());

                double yIntercept = regression.getIntercept();
                double slope = regression.getSlope();
                char plusOrMinusSymbol = yIntercept > 0 ? '+' : Character.MIN_VALUE;

                Label lineEquation = new Label("y = " + slope + "x " + plusOrMinusSymbol + yIntercept);



                VBox root = new VBox();
                root.getChildren().add(lineChart);


                root.getChildren().add(lineEquation);
                root.getChildren().add(rSquare);



                scenes.add(new Scene(root));

            }


        return (scenes);
    }

    private static Float parsingFloatValues(String replicateQuantOrRatioStd) {

        try {

            return Float.parseFloat(replicateQuantOrRatioStd.split(" ")[0]);

        } catch(NumberFormatException exc) {

            return 0.0f;
        }
    }

    private static Double parsingDoubleValues(String replicatePeakOrRetentionTime) {

        try {

            return Double.parseDouble(replicatePeakOrRetentionTime.split(" ")[0]);

        } catch (NumberFormatException exc) {

            return 0.00;
        }
    }
}
