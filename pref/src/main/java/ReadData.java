import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;

public class ReadData {
    private String inputFile;
    public String sister;
    public int rowcount = 0;

    //map with PNMs as the key and a list of matched sisters as the value
    public TreeMap<Double, ArrayList<String>> match = new TreeMap<>();

    //list of sisters who want to talk to a PNM
    public ArrayList<String> rankedSis = new ArrayList<>();

    //list of all pnms who are wanted by sisters
    public ArrayList<PNM> pnms = new ArrayList<>();


    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

//------------------------------------------------------------------------------------------------------------
    //----reads in excel file---------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------

    public void read() throws IOException  {

        File inputWorkbook = new File(inputFile);
        Workbook w;
        try {
            w = WorkbookFactory.create(inputWorkbook);

            //set up for reading sheet
            Sheet sheet = w.getSheetAt(0);

            //get number of rows
            int lastrow = sheet.getPhysicalNumberOfRows();

            //change to make minimum list length
            int row = 1200;


            //reads through excel sheet to pull data
            for (Row r : sheet) {
                //gets sister info

                Cell cell1 = r.getCell(0);
                Cell cell2 = r.getCell(1);
                Cell cell3 = r.getCell(2);


                //full name of sister
                if(cell2 != null) {
                    sister = cell2.getStringCellValue().toUpperCase() + " " + cell3.getStringCellValue().toUpperCase();
                    //System.out.println(sister);
                    rowcount++;
                }

                //formatting for while loop
                int p = 3;
                int counter = 0;
                int rank = 1;


                //recognizes the end of the doc
                if (rowcount==lastrow) {
                    break;
                } else {

                    while (counter < 10) { //sets limit at 10 bc the form only has max 10 spots to fill out

                        //checks for end of row
                        if(r.getCell(p)==null) {
                            break;
                        }

                        //sets up PNM
                        double id = r.getCell(p).getNumericCellValue();
                        String first = r.getCell(p + 1).getStringCellValue().toUpperCase();
                        String last = r.getCell(p + 2).getStringCellValue().toUpperCase();

                        PNM girl = new PNM(id, first, last, sister, 0);
                        girl.setPosition(rank);

                        pnms.add(girl);

                        //adds sisters with attached rank to PNM's list
                        String rSis = sister + " (" + rank + ")";
                        rankedSis.add(rSis);

                        //beginning of map build
                        if (!match.containsKey(girl.getID())) {
                            ArrayList<String> hmm = new ArrayList<>();
                            hmm.add(rSis);
                            match.put(girl.getID(), hmm);
                        }
                        else if(match.containsKey(girl.getID())) {
                            match.get(girl.getID()).add(rSis);
                        }

                        //sets up list for next data entry
                        rankedSis.clear();
                        p = p+4;
                        counter++;
                        rank++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//--------------------------------------------------------------------------------------------------------------
    //----formats map to be printed/exported--------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------

    public String printMap() {
        String export = "";

        for(Map.Entry<Double, ArrayList<String>> entry : match.entrySet()) {

            double key = entry.getKey();
            ArrayList<String> value = entry.getValue();

            if (key != 0.0) {
                export += "PNM " + key + ",--,";
/*
                for (PNM p : pnms) {
                    if (p.getID() == key) {
                        export += p.getFullName() + ",";
                    }
                }
*/
                for (String name : value) {
                    export += name + ",";
                }
                export += "\n";
            }
        }
        return export;
    }

    public void exportMap() {
        String fileName = "/Users/natalieeichorn/Desktop/extra/axid/prefMatching/RUSH_CRUSH_LIST.txt";
        try {
            PrintWriter outputStream = new PrintWriter(fileName);
            outputStream.println(printMap());
            outputStream.close();
            System.out.println("DONE :)\nPlease check the text file:\n" + fileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//------------------------------------------------------------------------------------------------------------
    //----main function---------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws IOException {
        System.out.println("*****THIS SHOWS THE PNM MATCHED WITH THE SISTERS WHO WOULD LIKE TO PREF THEM*****");
        System.out.println("**THE NUMBER IN () SHOW THE PRIORITY THAT THE SISTER RANKED THE PNM**");
        System.out.println("----------------------------------------------------------------------------");

        ReadData test = new ReadData();
        //ENTER FILE NAME WITH PATHWAY HERE!
        test.setInputFile("/Users/natalieeichorn/Desktop/extra/axid/prefMatching/RC1.xls");
        test.read();
        test.exportMap();
    }
}