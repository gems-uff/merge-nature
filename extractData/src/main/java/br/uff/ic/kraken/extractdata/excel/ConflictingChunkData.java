/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata.excel;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.sql.ConflictingChunkJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.ConflictingFileJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.RevisionJDBCDAO;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author gleiph
 */
public class ConflictingChunkData {

    public static final int PROJECT_ID = 0;
    public static final int PROJECT_NAME = 1;
    public static final int REVISION_SHA = 2;
    public static final int FILE_NAME = 3;
    public static final int CONFLICTING_CHUNK_IDENTIFIER = 4;
    public static final int OUTMOST_KIND_CONFLICT = 5;
    public static final int AMOUNT_LANGUAGE_CONSTRUCTS = 6;
    public static final int DEVELOPER_DECISION = 7;
    public static final int LOC_VERSION_1 = 8;
    public static final int LOC_VERSION_2 = 9;

    public static void main(String[] args) {

        String bdName = "automaticAnalysis";
        String outputPath = "/Users/gleiph/Desktop/automaticAnalyses/reportV1.0.xlsx";

        //Excel stuff
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Data");
        int rowNumber = 0;

        XSSFRow row = sheet.createRow(rowNumber);
        XSSFCell cell = row.createCell(PROJECT_ID);
        cell.setCellValue("Project ID");

        row = sheet.createRow(rowNumber++);
        cell = row.createCell(PROJECT_NAME);
        cell.setCellValue("Project name");

        cell = row.createCell(REVISION_SHA);
        cell.setCellValue("Revision SHA");

        cell = row.createCell(FILE_NAME);
        cell.setCellValue("File name");

        cell = row.createCell(CONFLICTING_CHUNK_IDENTIFIER);
        cell.setCellValue("Conflicting chunk identifier");

        cell = row.createCell(OUTMOST_KIND_CONFLICT);
        cell.setCellValue("Kind of conflict");

        cell = row.createCell(AMOUNT_LANGUAGE_CONSTRUCTS);
        cell.setCellValue("#Language constructs");

        cell = row.createCell(DEVELOPER_DECISION);
        cell.setCellValue("Developer decision");

        cell = row.createCell(LOC_VERSION_1);
        cell.setCellValue("#LOC Version 1");

        cell = row.createCell(LOC_VERSION_2);
        cell.setCellValue("#LOC Version 2");

        try (Connection connection = (new JDBCConnection()).getConnection(bdName)) {

            ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);
            RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO(connection);
            ConflictingFileJDBCDAO conflictingFileDAO = new ConflictingFileJDBCDAO(connection);
            ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(connection);

            List<Project> projects = projectDAO.selectAnalyzedMainProjects();

            for (Project project : projects) {

                System.out.println(project.getName());

                List<Revision> revisions = revisionDAO.selectByProjectId(project.getId());
                for (Revision revision : revisions) {

                    List<ConflictingFile> conflictingFiles = conflictingFileDAO.selectByRevisionId(revision.getId());
                    for (ConflictingFile conflictingFile : conflictingFiles) {

                        if (!conflictingFile.getName().toLowerCase().endsWith(".java")) {
                            continue;
                        }

                        List<ConflictingChunk> conflictingChunks = conflictingChunkDAO.selectByConflictingFileId(conflictingFile.getId());
                        for (ConflictingChunk conflictingChunk : conflictingChunks) {

                            row = sheet.createRow(rowNumber++);
                            cell = row.createCell(PROJECT_ID);
                            cell.setCellValue(project.getId());
//                            System.out.print(project.getId() + ", ");

                            cell = row.createCell(PROJECT_NAME);
                            cell.setCellValue(project.getName());
//                            System.out.print(project.getName() + ", ");

                            cell = row.createCell(REVISION_SHA);
                            cell.setCellValue(revision.getSha());
//                            System.out.print(revision.getSha() + ", ");

                            cell = row.createCell(FILE_NAME);
                            cell.setCellValue(conflictingFile.getName());
//                            System.out.print(conflictingFile.getName() + ", ");

                            cell = row.createCell(CONFLICTING_CHUNK_IDENTIFIER);
                            cell.setCellValue(conflictingChunk.getId());
//                            System.out.print(conflictingChunk.getIdentifier() + ", ");

                            String generalKindConflictOutmost = conflictingChunk.getGeneralKindConflictOutmost();
                            cell = row.createCell(OUTMOST_KIND_CONFLICT);
                            cell.setCellValue(generalKindConflictOutmost);
//                            System.out.print(generalKindConflictOutmost + ", ");

                            String[] languageConstructs = generalKindConflictOutmost.split(", ");
                            cell = row.createCell(AMOUNT_LANGUAGE_CONSTRUCTS);
                            cell.setCellValue(languageConstructs.length);
//                                                        System.out.print(languageConstructs.length + ", ");

                            String developerDecision = conflictingChunk.getDeveloperDecision().toString();
                            cell = row.createCell(DEVELOPER_DECISION);
                            cell.setCellValue(developerDecision);
//                            System.out.print(developerDecision + ", ");

                            int locVersion1 = conflictingChunk.getSeparatorLine() - conflictingChunk.getBeginLine() - 1;
                            int locVersion2 = conflictingChunk.getEndLine() - conflictingChunk.getSeparatorLine() - 1;

                            cell = row.createCell(LOC_VERSION_1);
                            cell.setCellValue(locVersion1);

                            cell = row.createCell(LOC_VERSION_2);
                            cell.setCellValue(locVersion2);
//                            System.out.println(locVersion1 + ", " + locVersion2);

                            if (rowNumber % 10 == 0) {
                                FileOutputStream out;
                                out = new FileOutputStream(outputPath);
                                wb.write(out);
                                out.close();

                            }

                        }
                    }
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(ConflictingChunkData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConflictingChunkData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConflictingChunkData.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            FileOutputStream out;
            out = new FileOutputStream(outputPath);
            wb.write(out);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConflictingChunkData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConflictingChunkData.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
