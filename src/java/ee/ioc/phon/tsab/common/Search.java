package ee.ioc.phon.tsab.common;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import ee.ioc.phon.tsab.dao.TsabDaoService;
import ee.ioc.phon.tsab.domain.Category;
import ee.ioc.phon.tsab.domain.Transcription;

public class Search {

  public static IndexWriter getLuceneWriter() throws TsabException {
    IndexWriter writer = null;
      try {
        writer = new IndexWriter(FSDirectory.open(Constants.luceneFolder), new StandardAnalyzer(Version.LUCENE_CURRENT),
            Constants.createNewLuceneIndex, IndexWriter.MaxFieldLength.LIMITED);
      } catch (CorruptIndexException e) {
        throw new TsabException("Failed to create Lucene writer!", e);
      } catch (LockObtainFailedException e) {
        throw new TsabException("Failed to create Lucene writer!", e);
      } catch (IOException e) {
        throw new TsabException("Failed to create Lucene writer!", e);
      }
    return writer;
  }
  

  public static void clearIndex() throws TsabException {
    IndexWriter writer = null;
    try {
      writer = new IndexWriter(FSDirectory.open(Constants.luceneFolder), new StandardAnalyzer(Version.LUCENE_CURRENT),
          true, IndexWriter.MaxFieldLength.LIMITED);
      writer.close();
    } catch (Exception e) {
      throw new TsabException("Failed to clear lucene index!", e);
    }
  }

  public static IndexReader getLuceneReader() throws TsabException {

    try {
      return IndexReader.open(FSDirectory.open(Constants.luceneFolder), true);
    } catch (Exception e) {
      throw new TsabException("Failed to get lucene reader!", e);
    }
    
  }

  public static IndexSearcher getLuceneSearcher() {
    IndexReader reader = null;
    try {
      reader = IndexReader.open(FSDirectory.open(Constants.luceneFolder), true);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return new IndexSearcher(reader);
  }

  public static Document getDocument(String code) {

    Query query = null;
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

    QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, "uid", analyzer);
    try {
      query = qp.parse(code);
      Searcher s = Search.getLuceneSearcher();
      TopDocs hits = s.search(query, 1);

      Document doc = s.doc(hits.scoreDocs[0].doc);

      return doc;

    } catch (Exception e) {
      return null;
    }
  }

  public static int getDocumentId(String code) {

    Query query = null;
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

    QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, "uid", analyzer);
    try {
      query = qp.parse(code);
      Searcher s = Search.getLuceneSearcher();
      TopDocs hits = s.search(query, 1);

      return hits.scoreDocs[0].doc;

    } catch (Exception e) {
      return 0;
    }
  }

  public static Query getQuery(String queryString) throws TsabException {

    Query query = null;
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

    MultiFieldQueryParser qp = new MultiFieldQueryParser(Version.LUCENE_CURRENT, new String[] { "title", "contents" },
        analyzer);
    try {
      query = qp.parse(queryString);

    } catch (ParseException e) {
      throw new TsabException("Failed to parse query!", e);
    }

    return query;
  }

  public static void indexTranscription(Transcription trans) throws TsabException {

    Date recorded = trans.getRecorded();
    String code = trans.getId().toString();
    String fn = trans.getFn();
    String title = trans.getTitle();
    Category category = trans.getCategory();

    List<String> v2 = TsabDaoService.getDao().getTranscriptionLines(trans);

    String lines = "";

    for (String h : v2) {
      String line = h;
      if (line != null) {
        lines += line + "\n";
      }
    }

    Document doc = new Document();

    doc.add(new Field("uid", code, Field.Store.YES, Field.Index.NOT_ANALYZED));

    if (recorded != null) {
      doc.add(new Field("modified", DateTools.dateToString(recorded, DateTools.Resolution.MINUTE), Field.Store.YES,
          Field.Index.NOT_ANALYZED));
    }

    if (category != null) {
      doc.add(new Field("category", category.getTitle(), Field.Store.YES, Field.Index.NOT_ANALYZED));
    }

    if (title != null) {
      doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
    }

    if (fn!= null) {
      doc.add(new Field("fn", fn, Field.Store.YES, Field.Index.ANALYZED));
    }

    if (lines != null) {
      doc.add(new Field("contents", lines, Field.Store.YES, Field.Index.ANALYZED));
    }

    IndexWriter writer = Search.getLuceneWriter();

    if (writer == null) {
      throw new TsabException("Could not open Lucene data files for writing");
    }

    Term tt = new Term("uid", code);
    try {
      writer.deleteDocuments(tt);

      writer.addDocument(doc);
      writer.optimize();
      writer.close();
    } catch (Exception e) {
      throw new TsabException("Failed to index transcription!", e);
    }

  }

  public static void removeTranscription(String id) throws TsabException {
    Term tt = new Term("uid", id);
    IndexWriter writer = Search.getLuceneWriter();
    try {
      writer.deleteDocuments(tt);
      writer.optimize();
      writer.close();
    } catch (Exception e) {
      throw new TsabException("Unable to remove transcription from the Lucene DB. Transcription id:"+id);
    }

  }
}
