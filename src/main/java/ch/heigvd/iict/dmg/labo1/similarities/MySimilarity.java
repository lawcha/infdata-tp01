package ch.heigvd.iict.dmg.labo1.similarities;

import org.apache.lucene.search.similarities.ClassicSimilarity;

public class MySimilarity extends ClassicSimilarity {

	// TODO student
	// Implement the functions described in section "Tuning the Lucene Score"

    @Override
    public float tf(float freq) { return (float) (1 + Math.log(freq)); }

    @Override
    public float idf(long docFreq, long docCount) {
        return (float) (Math.log(docCount/(docFreq+1f)) + 1f);
    }

    @Override
    public float lengthNorm(int numTerms) { return 1; }
}
