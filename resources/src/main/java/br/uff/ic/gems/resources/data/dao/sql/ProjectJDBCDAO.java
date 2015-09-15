/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.Language;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import java.sql.SQLException;

/**
 *
 * @author gleiph
 */
public class ProjectJDBCDAO {

    public static final String ID = "id";
    public static final String CREATED_AT = "createdat";
    public static final String DEVELOPERS = "developers";
    public static final String HTML_URL = "htmlurl";
    public static final String MESSAGE = "message";
    public static final String NAME = "name";
    public static final String CONFLICTING_MERGES = "numberconflictingmerges";
    public static final String MERGE_REVISIONS = "numbermergerevisions";
    public static final String REVISIONS = "numberrevisions";
    public static final String PRIVATE = "priva";
    public static final String REPOSITORY_PATH = "repositorypath";
    public static final String SEARCH_URL = "searchurl";
    public static final String UPDATED_AT = "updatedat";

    public void insert(Project project) throws SQLException {

        if (project.getId() == null) {
            throw new SQLException("Invalid identifier during insertion.");
        }

        String insertSQL = "INSERT INTO " + Tables.PROJECT
                + "(" + ID + ", " + CREATED_AT + ", " + DEVELOPERS + ", " + HTML_URL
                + ", " + MESSAGE + ", " + NAME + ", " + CONFLICTING_MERGES + ", "
                + MERGE_REVISIONS + ", " + REVISIONS + ", " + PRIVATE + ", "
                + REPOSITORY_PATH + ", " + SEARCH_URL + ", " + UPDATED_AT + ") "
                + "VALUES (\'" + project.getId() + "\', \'" + project.getCreatedAt() + "\', \'" + project.getDevelopers()
                + "\', \'" + project.getHtmlUrl() + "\', \'" + project.getMessage() + "\', \'" + project.getName()
                + "\', \'" + project.getNumberConflictingMerges() + "\', \'" + project.getNumberMergeRevisions() + "\', \'"
                + project.getNumberRevisions() + "\', \'" + project.isPriva() + "\', \'" + project.getRepositoryPath()
                + "\', \'" + project.getSearchUrl() + "\', \'" + project.getUpdatedAt() + "\')";

        DefaultOperations.insert(insertSQL);

    }

    public void insertAll(Project project) throws SQLException {
        this.insert(project);

        //Adding languages
        LanguageJDBCDAO languageDAO = new LanguageJDBCDAO();

        for (Language language : project.getLanguages()) {
            languageDAO.insertAll(language, project.getId());
        }

        //Adding revisions
        RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO();

        for (Revision revision : project.getRevisions()) {
            revisionDAO.insertAll(revision, project.getId());
        }

    }

//    public static void main(String[] args) {
//
//        File projectFile;
//        projectFile = new File("/Users/gleiph/Desktop/teste/outTeste/platform_dalvik/platform_dalvik");
//        
//        String revisionFile = "/Users/gleiph/Desktop/teste/outTeste/platform_dalvik/0/3cfe50e0fa88e9aeee2739bebba6c504711ef47c";
//
//        Project project = AutomaticAnalysis.readProject(projectFile);
//
//        Revision revision = AutomaticAnalysis.readRevision(revisionFile);
//
//        ProjectJDBCDAO projectDAO = new ProjectJDBCDAO();
//        RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO();
//
//        try {
//            projectDAO.insert(project);
//            revisionDAO.insertAll(revision, project.getId());
//
//        } catch (SQLException ex) {
//            Logger.getLogger(ProjectJDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
////        ProjectJDBCDAO projectDAO = new ProjectJDBCDAO();
////        RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO();
////        LanguageJDBCDAO languageDAO = new LanguageJDBCDAO();
////        ConflictingFileJDBCDAO conflictingFileDAO = new ConflictingFileJDBCDAO();
////        ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO();
////        KindConflictJDBCDAO kindConflictDAO = new KindConflictJDBCDAO();
////        LanguageConstructJDBCDAO languageConstructDAO = new LanguageConstructJDBCDAO();
////        SolutionContentJDBCDAO chunkContentDAO = new SolutionContentJDBCDAO();
////
////        Project project = new Project();
////        project.setCreatedAt("ghj");
////        project.setDevelopers(12);
////        project.setHtmlUrl("kjlkj");
////        project.setId(1l);
////        project.setMessage("message");
////        project.setName("teste");
////        project.setNumberConflictingMerges(1);
////        project.setNumberMergeRevisions(2);
////        project.setNumberRevisions(2);
////        project.setPriva(true);
////        project.setRepositoryPath("/jsldjk");
////        project.setSearchUrl("http");
////        project.setUpdatedAt("bla");
////
////        Revision revision = new Revision();
////        revision.setBaseSha("sjdskhd");
////        revision.setLeftSha("ksjdlskj");
////        revision.setNumberConflictingFiles(1);
////        revision.setNumberJavaConflictingFiles(1);
////        revision.setRightSha("kjlkjkl");
////        revision.setSha("jshdksjhd");
////        revision.setStatus(MergeStatus.OCTOPUS);
////
////        Language language = new Language();
////        language.setName("Java");
////        language.setPercentage(100);
////        language.setSize(10);
////
////        ConflictingFile conflictingFile = new ConflictingFile();
////        conflictingFile.setFileType("java");
////        conflictingFile.setName("something.java");
////        conflictingFile.setPath("/sjlks/something.java");
////        conflictingFile.setRemoved(false);
////
////        ConflictingChunk conflictingChunk = new ConflictingChunk();
////        conflictingChunk.setBeginLine(1);
////        conflictingChunk.setDeveloperDecision(DeveloperDecision.VERSION1);
////        conflictingChunk.setEndLine(12);
////        conflictingChunk.setIdentifier("cc1");
////        conflictingChunk.setSeparatorLine(6);
////
////        KindConflict kindConflict = new KindConflict();
////        kindConflict.setBeginLine(2);
////        kindConflict.setEndLine(4);
////
////        LanguageConstruct languageConstruct = new LanguageConstruct();
////        languageConstruct.setBeginColumn(1);
////        languageConstruct.setBeginColumnBlock(2);
////        languageConstruct.setBeginLine(3);
////        languageConstruct.setBeginLineBlock(4);
////        languageConstruct.setEndColumn(5);
////        languageConstruct.setEndColumnBlock(6);
////        languageConstruct.setEndLine(7);
////        languageConstruct.setEndLineBlock(8);
////        languageConstruct.setHasBlock(true);
////        languageConstruct.setName("Method declaration");
////
////        try {
////            projectDAO.insert(project);
////            Long revision_id = revisionDAO.insert(revision, project.getId());
////            Long language_id = languageDAO.insert(language, project.getId());
////            Long conflictingFile_id = conflictingFileDAO.insert(conflictingFile, revision_id);
////            Long conflictingChunk_id = conflictingChunkDAO.insert(conflictingChunk, conflictingFile_id);
////            Long kindConflict_id = kindConflictDAO.insert(kindConflict, conflictingChunk_id, Side.LEFT);
////            Long languageConstruct_id = languageConstructDAO.insert(languageConstruct, kindConflict_id);
////
////            chunkContentDAO.insert("a", conflictingChunk_id);
////        } catch (SQLException ex) {
////            Logger.getLogger(ProjectJDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
//    }
}
