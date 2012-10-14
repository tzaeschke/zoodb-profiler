package org.zoodb.profiling.api;

import java.util.List;

import org.zoodb.profiling.api.impl.PathTreeNode;

public interface IPathTreeNode {

	public Activation getItem();

	public Object getClazz();

	public List<IPathTreeNode> getChildren();

	public IPathTreeNode getPathNode(Object predecessor);

	public IPathTreeNode getPathNode(String clazz, String ref, String oid);

	public void prettyPrint(int indent);

	public List<Class> getActivatorClasses(List<Class> classList);

	public IPathTreeNode getPathNodeClass(IPathTreeNode currentNode);

	public boolean isList();

	public void incAccessFrequency();

	public void addChildren(PathTreeNode newChild);
	
	/**
	 * Returns the path node which is of class 'clazzName' and has objectIdentifier 'oid'.
	 * Returns null if such a nodes does not exist. 
	 * @param clazzName
	 * @param oid
	 * @return
	 */
	public IPathTreeNode getNode(String clazzName, String oid);

	public void prettyPrintWithTrigger(int indent);

}
