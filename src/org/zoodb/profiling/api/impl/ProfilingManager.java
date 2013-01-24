package org.zoodb.profiling.api.impl;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zoodb.jdo.TransactionImpl;
import org.zoodb.jdo.api.impl.DBStatistics;
import org.zoodb.profiling.ProfilingConfig;
import org.zoodb.profiling.analyzer.AnalyzerPipeline;
import org.zoodb.profiling.analyzer.ClassMergeAnalyzer;
import org.zoodb.profiling.analyzer.ClassSplitAnalyzer;
import org.zoodb.profiling.analyzer.CollectionAggregAnalyzer;
import org.zoodb.profiling.analyzer.CollectionAnalyzer;
import org.zoodb.profiling.analyzer.FieldAccessAnalyzer;
import org.zoodb.profiling.analyzer.LOBAnalyzer;
import org.zoodb.profiling.analyzer.ReferenceShortcutAnalyzerP;
import org.zoodb.profiling.analyzer.UnusedFieldsAnalyzer;
import org.zoodb.profiling.api.IDataExporter;
import org.zoodb.profiling.api.IDataProvider;
import org.zoodb.profiling.api.IFieldManager;
import org.zoodb.profiling.api.IPathManager;
import org.zoodb.profiling.api.IProfilingManager;
import org.zoodb.profiling.api.ITrxManager;
import org.zoodb.profiling.event.Events;

import ch.ethz.globis.profiling.commons.suggestion.AbstractSuggestion;

/**
 * @author tobiasg
 *
 */
public class ProfilingManager implements IProfilingManager {
	
	private static Logger logger = LogManager.getLogger("allLogger");
	
	private static ProfilingManager singleton = null;
	
	private Date begin;
	private Date end;
	
	private IPathManager pathManager;
	private IFieldManager fieldManager;
	private QueryManager queryManager;
	private ITrxManager trxManager;
	private ClassSizeManager csManager;
	
	private Collection<AbstractSuggestion> suggestions;
	
	private static String currentTrxId;
	
	private boolean collectActivations = true;
	
	
	public static ProfilingManager getInstance() {
		if (singleton == null) {
			singleton = new ProfilingManager();
		}
		return singleton;
	}
	
	private ProfilingManager() {
		pathManager = new PathManagerTreeV2();
		fieldManager = new FieldManager();
		suggestions = new LinkedList<AbstractSuggestion>();
		trxManager = new TrxManager();
		csManager = new ClassSizeManager();
		
		if (ProfilingConfig.ENABLE_QUERY_PROFILING) {
			queryManager = new QueryManager();
			ProfilingQueryListener queryListener = new ProfilingQueryListener();
			Events.register(queryListener);
		}
		
		ProfilingTrxListener trxListener = new ProfilingTrxListener(this);
		Events.register(trxListener);
	}
	
	@Override
	public void save() {
		IDataExporter exporter = new XMLExporter(begin,end);
		
		exporter.exportSuggestions(suggestions);
		
		if (queryManager != null) {
			exporter.exportQueries(queryManager.getQueryProfiles());
		}
		
		exporter.exportClassStatistics(null);
	}

	@Override
	public IPathManager getPathManager() {
		return pathManager;
	}
	@Override
	public IFieldManager getFieldManager() {
		return fieldManager;
	}
	@Override
	public ITrxManager getTrxManager() {
		return trxManager;
	}
	public QueryManager getQueryManager() {
		return queryManager;
	}
	public ClassSizeManager getClassSizeManager() {
		return csManager;
	}

	@Override
	public void newTrxEvent(TransactionImpl trx) {
		logger.info("New Trx: " + trx.getUniqueTrxId());
		currentTrxId = trx.getUniqueTrxId();
	}
	
	public String getCurrentTrxId() {
		return currentTrxId;
	}

	@Override
	public IDataProvider getDataProvider() {
		// TODO dataprovider should be a singleton
		ProfilingDataProvider dp = new ProfilingDataProvider();
		dp.setFieldManager((FieldManager) fieldManager);
		return dp;
	}

	@Override
	public void finish() {
		end = new Date();

		//data types
		//TODO: move the analyzing functino from fieldmanager to fieldaccessanalyer

		AnalyzerPipeline ap = new AnalyzerPipeline();
		
		if (ProfilingConfig.ENABLE_ANALYZER_CLASS_SPLIT) {
			ap.addAnalyzer(new ClassSplitAnalyzer());
		}
		if (ProfilingConfig.ENABLE_ANALYZER_SHORTCUTS) {
			ap.addAnalyzer(new ReferenceShortcutAnalyzerP());
		}
		if (ProfilingConfig.ENABLE_ANALYZER_AGGREGATION) {
			ap.addAnalyzer(new CollectionAggregAnalyzer());
		}
		if (ProfilingConfig.ENABLE_ANALYZER_UNUSED_COLLECTION) {
			ap.addAnalyzer(new CollectionAnalyzer());
		}
		if (ProfilingConfig.ENABLE_ANALYZER_UNUSED_FIELDS) {
			ap.addAnalyzer(new UnusedFieldsAnalyzer());
		}
		if (ProfilingConfig.ENABLE_ANALYZER_LOB) {
			ap.addAnalyzer(new LOBAnalyzer());
		}
		if (ProfilingConfig.ENABLE_ANALZYER_CLASS_MERGE) {
			ap.addAnalyzer(new ClassMergeAnalyzer());
		}
		
		ap.startPipeline();
		
		suggestions.addAll(ap.getSuggestions());
	}

	@Override
	public void init() {
		DBStatistics.enable(true);
		begin = new Date();
	}

	@Override
	public void addSuggestion(AbstractSuggestion s) {
		suggestions.add(s);
	}

	@Override
	public void addSuggestions(Collection<AbstractSuggestion> s) {
		suggestions.addAll(s);
	}
	
	public static Logger getProfilingLogger() {
		return logger;
	}

	public boolean isCollectActivations() {
		return collectActivations == true && currentTrxId != null;
	}

	public void setCollectActivations(boolean collectActivations) {
		this.collectActivations = collectActivations;
	}


	

}