/*
 * Copyright (C) 2011 Marco Aurélio Graciotto Silva <magsilva@ironiacorp.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ironiacorp.scienceanalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import com.ironiacorp.graph.layout.Graphviz;
import com.ironiacorp.graph.layout.GraphvizGraph;
import com.ironiacorp.graph.model.DirectedEdge;
import com.ironiacorp.graph.model.DirectedEdge.NodeType;
import com.ironiacorp.graph.model.basic.BasicDirectedEdge;
import com.ironiacorp.graph.model.basic.BasicGraph;
import com.ironiacorp.graph.model.basic.BasicNode;
import com.ironiacorp.graph.model.Edge;
import com.ironiacorp.graph.model.Graph;
import com.ironiacorp.graph.model.GraphElement;
import com.ironiacorp.graph.model.Node;



public class AdvisingGraph
{
	public Graph getGraphFor(String name)
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/Alumni/ICMC/alumni-icmc-posgrad-ccmc.csv");
		return getGraphFor(is, name);
	}

	/*
	public Graph getGraphFor(InputStream is, String name)
	{
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		String[] line;
		Graph graph = new Graph();
		int count = 0;
		Map<String, Node> people = new HashMap<String, Node>();
		
		graph.setType(Graph.GraphType.DIRECTED);
		try {
			while ((line = reader.readNext()) != null) {
		    	String degree = line[0].trim();
		    	String title = line[1].trim();
		    	String advisee = line[2].trim();
		    	String advisor = line[3].trim();
		    	DirectedEdge edge = new DirectedEdge();
		    	Node adviseeNode = null;
		    	Node advisorNode = null;
		    	
		    	edge.setId(count);
		    	count++;
		    	if (advisee.equalsIgnoreCase(name) || advisor.equalsIgnoreCase(name)) {
		    		adviseeNode = people.get(advisee.toLowerCase());
		    		if (adviseeNode == null) {
		    			adviseeNode = new Node();
		    			adviseeNode.setId(count);
		    			adviseeNode.setLabel(advisee);
		    			people.put(advisee.toLowerCase(), adviseeNode);
		    			count++;
		    		}
		    		
		    		advisorNode = people.get(advisor.toLowerCase());
		    		if (advisorNode == null) {
		    			advisorNode = new Node();
		    			advisorNode.setId(count);
		    			advisorNode.setLabel(advisor);
		    			people.put(advisor.toLowerCase(), advisorNode);
		    			count++;
		    		}
		    	}
		    	
		    	if (adviseeNode != null && advisorNode != null) {
		    		edge.addNode(advisorNode, NodeType.DEST);
		    		edge.addNode(adviseeNode, NodeType.SOURCE);
		    		graph.addElement(edge);
		    	}
			}
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}

		for (Node node : people.values()) {
			graph.addElement(node);
		}
		
		
		return graph;
	}
	*/

	public Graph getGraphFor(InputStream is)
	{
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		String[] line;
		Graph graph = new BasicGraph();
		int count = 0;
		Map<String, Node> people = new HashMap<String, Node>();
		
		graph.setType(Graph.GraphType.DIRECTED);
		try {
			while ((line = reader.readNext()) != null) {
		    	String degree = line[0].trim();
		    	String title = line[1].trim();
		    	String advisee = line[2].trim();
		    	String advisor = line[3].trim();
		    	DirectedEdge edge = new BasicDirectedEdge();
		    	Node adviseeNode = null;
		    	Node advisorNode = null;
		    	
		    	edge.setId(count);
		    	count++;
	    		adviseeNode = people.get(advisee.toLowerCase());
	    		if (adviseeNode == null) {
	    			adviseeNode = new BasicNode();
		    		adviseeNode.setId(count);
		    		adviseeNode.setAttribute("label", advisee);
		    		people.put(advisee.toLowerCase(), adviseeNode);
		    		count++;
		    	}
		    		
		    	advisorNode = people.get(advisor.toLowerCase());
		    	if (advisorNode == null) {
		    		advisorNode = new BasicNode();
		    		advisorNode.setId(count);
		    		advisorNode.setAttribute("label", advisor);
		    		people.put(advisor.toLowerCase(), advisorNode);
		    		count++;
		    	}
		    	
		    	if (adviseeNode != null && advisorNode != null) {
		    		edge.addNode(advisorNode, NodeType.DEST);
		    		edge.addNode(adviseeNode, NodeType.SOURCE);
		    		graph.addElement(edge);
		    	}
			}
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}

		for (Node node : people.values()) {
			graph.addElement(node);
		}
		
		return graph;
	}
	
	public Graph getGraphFor(InputStream is, String name)
	{
		Set<Node> selectedNodes = new HashSet<Node>();
		boolean hasBeenModified = true;
		int level = 0;
		Graph graph = getGraphFor(is);
	
		for (GraphElement element : graph.getElements()) {
			if (element instanceof Node) {
				Node node = (Node) element;
				if (name.equalsIgnoreCase((String) element.getAttribute("label"))) {
					hasBeenModified = selectedNodes.add(node);
				}
			}
		}
		
		while (hasBeenModified) {
			Set<Node> newlySelectedNodes = new HashSet<Node>();
			for (GraphElement element : graph.getElements()) {
				/*
				if (element instanceof Edge) {
					Edge edge = (Edge) element;
					Set<Node> edgeNodes = edge.getNodes();
					for (Node node : selectedNodes) {
						if (edgeNodes.contains(node)) {
							newlySelectedNodes.addAll(edgeNodes);
							break;
						}
					}
				}
				*/
				if (element instanceof DirectedEdge) {
					DirectedEdge edge = (DirectedEdge) element;
					Set<Node> edgeNodes = edge.getNodes(NodeType.DEST);
					for (Node node : selectedNodes) {
						if (edgeNodes.contains(node)) {
							newlySelectedNodes.addAll(edge.getNodes(NodeType.SOURCE));
							break;
						}
					}
				}
				
			}
			hasBeenModified = selectedNodes.addAll(newlySelectedNodes);
			level++;
		}
		
		Iterator<GraphElement> i = graph.getElements().iterator();
		while (i.hasNext()) {
			GraphElement element = i.next();
			if (element instanceof Edge) {
				Edge edge = (Edge) element;
				Set<Node> edgeNodes = edge.getNodes();
				if (edgeNodes.retainAll(selectedNodes)) {
					i.remove();
				}
			}
			if (element instanceof Node) {
				Node node = (Node) element;
				if (! selectedNodes.contains(node)) {
					i.remove();
				}
			}
		}
		
		return graph;
	}
}
