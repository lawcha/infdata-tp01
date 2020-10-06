package ch.heigvd.iict.dmg.labo1.indexer;

import ch.heigvd.iict.dmg.labo1.parsers.ParserListener;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.apache.lucene.document.LongPoint.pack;

public class CACMIndexer implements ParserListener {
	
	private Directory 	dir 			= null;
	private IndexWriter indexWriter 	= null;
	
	private Analyzer 	analyzer 		= null;
	private Similarity 	similarity 		= null;
	
	public CACMIndexer(Analyzer analyzer, Similarity similarity) {
		this.analyzer = analyzer;
		this.similarity = similarity;
	}
	
	public void openIndex() {
		// 1.2. create an index writer config
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE); // create and replace existing index
		iwc.setUseCompoundFile(false); // not pack newly written segments in a compound file: 
		//keep all segments of index separately on disk
		if(similarity != null)
			iwc.setSimilarity(similarity);
		// 1.3. create index writer
		Path path = FileSystems.getDefault().getPath("index");
		try {
			this.dir = FSDirectory.open(path);
			this.indexWriter = new IndexWriter(dir, iwc);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onNewDocument(Long id, String authors, String title, String summary) {
		Document doc = new Document();

		// TODO student: add to the document "doc" the fields given in
		// parameters. You job is to use the right Field and FieldType
		// for these parameters.
		if (id != null) {
			FieldType type = getDefaultType();
			type.setStoreTermVectors(true);
			Field idField = new Field("id", id + "", type);
			doc.add(idField);
		}
		if (authors != null) {
			for (String a : authors.split(";")) {
				FieldType type = getDefaultType();
				//type.setStoreTermVectors(true);
				Field authorField = new Field("author", a, type);
				doc.add(authorField);
			}
		}
		if (title != null) {
			FieldType type = getDefaultType();
			type.setStoreTermVectors(true);
			Field titleField = new Field("title", title, type);
			doc.add(titleField);
		}
		if (summary != null) {
			FieldType type = getDefaultType();
			type.setStoreTermVectors(true);
			type.setStoreTermVectorOffsets(true);
			Field summaryField = new Field("summary", summary, type);
			doc.add(summaryField);
		}
		try {
			this.indexWriter.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FieldType getDefaultType() {
		FieldType type = new FieldType();
//		type.setOmitNorms(true);
		type.setIndexOptions(IndexOptions.DOCS);
		type.setStored(true);
		type.setTokenized(false);
		return type;
	}
	
	public void finalizeIndex() {
		if(this.indexWriter != null)
			try { this.indexWriter.close(); } catch(IOException e) { /* BEST EFFORT */ }
		if(this.dir != null)
			try { this.dir.close(); } catch(IOException e) { /* BEST EFFORT */ }
	}
	
}
