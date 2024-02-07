# Heron_Data_Installer

**Coming Soon**
**[New Repository Here](https://github.com/Step-henC/heron-data-internet)**
This will now become a publicly accessible website in the near future using React js. Video below of progress so far.




https://github.com/Step-henC/Heron_Data_Installer/assets/98792412/e3e255a4-3c25-44d7-abbe-2a15ec5e0ee0



# What is This?

In the field of Mass Spectrometry, scientists use the Skyline from the MacCoss Lab Software to analyze peptide data, mainly calibration curves. The Skyline software proceeds to issue a CSV file with raw data. The problem 
is that calibration curves are established from calibration standards that are ran in triplicates. However, Skyline CSV does not treat data in triplicates. The result is researchers spending hours
reformatting their data to analyze in triplicates. Heron Data is a program that takes the resulting Skyline Data and returns graphs of the calibration curves, as well as a PDF and CSV for further analysis and sharing.

# How Does Heron Data Do this?
Heron Data uses Java and Java Swing/JavaFX for the GUI. The code was then made into an executable jar in intelliJ. Then, that executable jar was used to make an executable file bunded with the Amazon Corretto 8 JRE (java Runtime Environment) so that end users would not have
to download and configure java on host computers. Then, that executable file was used to make an installer using Inno Setup Compiler for users to download Heron Data. In the future, the goal is to make this project
accessible to everyone on most likely, SourceForge.net.


# How Do I use this repo?
Make sure you have java and maven. Use an IDE of your choice. Type "mvn clean install -U" in the command line and then " mvn spring-boot:run" the project. A sample file is the "big data peptide.csv" found in the project files.

# Disclaimer
The goal was to have the entire installer, but that will have to be troublshot for another time. 

# Future goals
Configure this project with docker for cross-platform compatibility
