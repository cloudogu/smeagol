package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.Path;
import com.cloudogu.smeagol.wiki.domain.SearchResult;
import com.cloudogu.smeagol.wiki.domain.SearchResultRepository;
import com.cloudogu.smeagol.wiki.domain.WikiId;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Lucene based implementation of a {@link SearchResultRepository}.
 */
@Service
public class LuceneSearchResultRepository implements SearchResultRepository {

    private static final String DEFAULT_QUERY_FIELD = "content";

    private final LuceneContext context;

    @Autowired
    public LuceneSearchResultRepository(LuceneContext context) {
        this.context = context;
    }

    @Override
    public Iterable<SearchResult> search(WikiId wikiId, String queryString) {
        List<SearchResult> searchResults = Lists.newArrayList();

        try(IndexReader reader = context.createReader(wikiId)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            Query query = createQuery(queryString);
            TopDocs docs = searcher.search(query, 25);

            for (ScoreDoc hit : docs.scoreDocs) {
                SearchResult result = createSearchResultFromHit(searcher, wikiId, hit);
                searchResults.add(result);
            }

        } catch (IOException | ParseException e) {
            throw Throwables.propagate(e);
        }
        return searchResults;
    }

    private Query createQuery(String queryString) throws ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser(DEFAULT_QUERY_FIELD, analyzer);

        return queryParser.parse(queryString);
    }

    private SearchResult createSearchResultFromHit(IndexSearcher searcher, WikiId wikiId, ScoreDoc hit) throws IOException {
        Document document = searcher.doc(hit.doc);
        String path = document.get(LuceneFields.PATH);
        return new SearchResult(wikiId, Path.valueOf(path));
    }

}
