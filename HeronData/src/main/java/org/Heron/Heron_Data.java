package org.Heron;


import org.Heron.repository.ReadingFile;

import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import java.net.URL;


public class Heron_Data {



    public static void main(String[] args) {

SwingUtilities.invokeLater(new Runnable() {

    @Override
    public void run() {

        JFrame frame = new JFrame("Heron Data");

        JButton chooseFileButton = new JButton("Choose CSV File");

        JPanel panel = new JPanel();


        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                    String filePath = "";
                    JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV FILES", "csv", ".csv");
                    chooser.setFileFilter(filter);

                    int r = chooser.showOpenDialog(null);
                    if (r == JFileChooser.APPROVE_OPTION) {

                        filePath += chooser.getSelectedFile().getAbsolutePath();

                        try {
                            ReadingFile.readFile(filePath.replace("\"", ""));

                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, e.getMessage(), "File Read Error", JOptionPane.ERROR_MESSAGE);

                        }

                    }
                    frame.setVisible(false);
            }
        });



        panel.add(chooseFileButton);



        URL ur = this.getClass().getResource("/images/heron_logo.jpg");
        Image im = Toolkit.getDefaultToolkit().getImage(ur);

        JLabel l = new JLabel();
        l.setIcon(new ImageIcon(new ImageIcon(im).getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT)));


        frame.add(panel, BorderLayout.CENTER);
        JPanel pic = new JPanel();
        pic.add(l);
        frame.add(pic, BorderLayout.NORTH);


        frame.setSize(300, 300);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);


    }
});
    }
}

//    MIT License
//
//    Copyright (c) 2023 Stephen E. Cunningham
//
//        Permission is hereby granted, free of charge, to any person obtaining a copy
//        of this software and associated documentation files (the "Software"), to deal
//        in the Software without restriction, including without limitation the rights
//        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//        copies of the Software, and to permit persons to whom the Software is
//        furnished to do so, subject to the following conditions:
//
//        The above copyright notice and this permission notice shall be included in all
//        copies or substantial portions of the Software.
//
//        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//        SOFTWARE.
