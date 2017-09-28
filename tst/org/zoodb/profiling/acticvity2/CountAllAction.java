package org.zoodb.profiling.acticvity2;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.zoodb.profiling.model2.Author;
import org.zoodb.profiling.model2.Conference;
import org.zoodb.profiling.model2.ConferenceSeries;
import org.zoodb.profiling.model2.Publication;
import org.zoodb.profiling.model2.PublicationSplit;
import org.zoodb.profiling.model2.Tags;
import org.zoodb.profiling.simulator.IAction;

/**
 * Dummy-action that iterates through extends and
 *
 */
public class CountAllAction implements IAction {

	@Override
	public Object executeAction(PersistenceManager pm) {
		
		pm.currentTransaction().begin();
		
		Extent<Author> authors = pm.getExtent(Author.class);
		Extent<Publication> publications = pm.getExtent(Publication.class);
		Extent<Conference> conferences = pm.getExtent(Conference.class);
		Extent<ConferenceSeries> series = pm.getExtent(ConferenceSeries.class);
		Extent<Tags> tags = pm.getExtent(Tags.class);
		Extent<PublicationSplit> splits = pm.getExtent(PublicationSplit.class);

		int i=0;
		for (Author a : authors) {
			//System.out.println(a.getName());
			i++;
		}
		System.out.println("Number of persistent authors:" + i);
		i=0;
		int noConference = 0;
		for (Publication p : publications) {
			//if (p.getConference() == null) noConference++;
			i++;
		}
		System.out.println("Number of persistent publications:" + i + " (" + noConference + ")");
		i=0;
		int noSeries = 0;
		for (Conference c : conferences) {
			//if (c.getSeries() == null) noSeries++;
			i++;
		}
		System.out.println("Number of persistent conferences:" + i + " (" + noSeries + ")");
		i=0;
		for (ConferenceSeries s : series) {
			//System.out.println(s.getDBLPkey());
			i++;
		}
		System.out.println("Number of persistent conference series:" + i);
		i=0;
		for (Tags t : tags) {
			//System.out.println(t.getLabel());
			i++;
		}
		System.out.println("Number of persistent tags:" + i);
		i=0;
		for (PublicationSplit s : splits) {
			//System.out.println(t.getLabel());
			i++;
		}
		System.out.println("Number of persistent publicationsplits:" + i);
		
		
		pm.currentTransaction().commit();
		
		return null;
	}

}
