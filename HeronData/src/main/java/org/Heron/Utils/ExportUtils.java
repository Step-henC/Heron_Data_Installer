package org.Heron.Utils;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExportUtils {

    public static void exportToCSV(JTable tableToExport, JTable nextTableToExport,
                                      File pathToExportTo) {

        try {

            TableModel model = tableToExport.getModel();
            TableModel secondModel = nextTableToExport.getModel();
            FileWriter csv = new FileWriter(pathToExportTo);

            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.write(model.getColumnName(i) + ",");
            }

            csv.write("\n");

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    if (model.getValueAt(i, j) == null) {
                        model.setValueAt("", i, j);
                        //continue;
                    }
                    csv.write(model.getValueAt(i, j).toString() + ",");
                }
                csv.write("\n");
            }

            //next table
            csv.write("\n");
            for (int i = 0; i < secondModel.getColumnCount(); i++) {
                csv.write(secondModel.getColumnName(i) + ",");
            }

            csv.write("\n");

            for (int i = 0; i < secondModel.getRowCount(); i++) {
                for (int j = 0; j < secondModel.getColumnCount(); j++) {
                    csv.write(secondModel.getValueAt(i, j).toString() + ",");
                }
                csv.write("\n");
            }


            csv.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "File Export Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            JOptionPane.showMessageDialog(null, "Data successfully exported to CSV", "CSV Export Success", JOptionPane.PLAIN_MESSAGE);

        }
    }

    public static void exportToPdf(JPanel component, File fileName) {
        Document d = new Document();
        try{


           PdfWriter writer = PdfWriter.getInstance(d, new FileOutputStream(fileName));
           d.open();

            com.itextpdf.text.Image iTextImage = com.itextpdf.text.Image.getInstance(writer, ExportUtils.getImageFromPanel(component), 1 );


            iTextImage.scalePercent(25);
            iTextImage.setRotationDegrees(90);


            d.add(iTextImage);
        }catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "File Export Error", JOptionPane.ERROR_MESSAGE);

        } finally {
            if (d.isOpen()) {
                d.close();
            }
            JOptionPane.showMessageDialog(null, "Data successfully exported to PDF", "PDF Export Success", JOptionPane.PLAIN_MESSAGE);
        }

    }

    public static java.awt.Image getImageFromPanel(JPanel component) {
        BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
        component.paint(image.getGraphics());
        return image;
    }
}
