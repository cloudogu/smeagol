package com.cloudogu.smeagol.wiki.infrastructure;

import com.cloudogu.smeagol.wiki.domain.*;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
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

            Analyzer analyzer = context.createAnalyzer();

            Query query = createQuery(analyzer, queryString);
            TopDocs docs = searcher.search(query, 25);

            Highlighter highlighter = createHighlighter(query);

            for (ScoreDoc hit : docs.scoreDocs) {
                Document document = searcher.doc(hit.doc);

                String bestFragment = findBestFragment(analyzer, highlighter, document);
                SearchResult result = createSearchResultFromHit(wikiId, hit, document, bestFragment);

                searchResults.add(result);
            }

        } catch (IOException | ParseException | InvalidTokenOffsetsException e) {
            throw Throwables.propagate(e);
        }
        return searchResults;
    }

    private String findBestFragment(Analyzer analyzer, Highlighter highlighter, Document document) throws IOException, InvalidTokenOffsetsException {
        String text = document.get(LuceneFields.CONTENT);
        return highlighter.getBestFragment(analyzer, LuceneFields.CONTENT, text);
    }

    private Highlighter createHighlighter(Query query) {
        Formatter formatter = new SimpleHTMLFormatter();
        QueryScorer scorer = new QueryScorer(query);

        Highlighter highlighter = new Highlighter(formatter, scorer);
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));

        return highlighter;
    }

    private Query createQuery(Analyzer analyzer, String queryString) throws ParseException {
        QueryParser queryParser = new QueryParser(DEFAULT_QUERY_FIELD, analyzer);
        return queryParser.parse(queryString);
    }

    private SearchResult createSearchResultFromHit(WikiId wikiId, ScoreDoc hit, Document document, String bestFragment) {
        String path = document.get(LuceneFields.PATH);
        return new SearchResult(wikiId, Path.valueOf(path), Score.valueOf(hit.score), ContentFragment.valueOf(bestFragment));
    }

}
