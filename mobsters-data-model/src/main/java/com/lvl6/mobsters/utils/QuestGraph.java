package com.lvl6.mobsters.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.lvl6.mobsters.po.staticdata.Quest;

public class QuestGraph {
	  private class Vertex {
	    private int nodeId;
	    private List<Integer> requiredVertices;

	    public Vertex(int id, List<Integer> required) {
	      nodeId = id;
	      requiredVertices = required;
	    }

	    public int getNodeId() {
	      return nodeId;
	    }

	    public List<Integer> getRequiredVertices() {
	      return requiredVertices;
	    }

	    public String toString() {
	    	StringBuilder sb = new StringBuilder();
	    	sb.append(nodeId);
	    	sb.append(": ");
	    	for (int x : requiredVertices) {
	    		sb.append(x);
	    		sb.append(", ");
	    	}
	    	
	    	sb.insert(0, "{");
	    	return sb.substring(0, sb.length()-2)+"}";
	    }
	  }

	  private ArrayList<Vertex> questVertices;

	  public QuestGraph(Collection<Quest> quests) {
	    questVertices = new ArrayList<Vertex>(quests.size());
	    for (Quest quest : quests) {
	      questVertices.add(new Vertex(quest.getId(), quest.getQuestsRequiredForThisAsList()));
	    }
	  }

	  public List<Integer> getQuestsAvailable(List<Integer> redeemed, List<Integer> inProgress) {
	    ArrayList<Integer> available = new ArrayList<Integer>();

	    for (Vertex v : questVertices) {
	    	//search for quests the user has not redeemed nor is in
	    	//the process of completing
	      if (redeemed == null || !redeemed.contains(v.getNodeId())) {
	        if (inProgress == null || !inProgress.contains(v.getNodeId())) {
	        	
	          if (v.getRequiredVertices().isEmpty() || 
	              (redeemed != null && redeemed.containsAll(v.getRequiredVertices()))) {
	          	//for all stand alone quests (quests with no prerequisites) OR
	          	//quests that the user has finished all the prerequisites, return these
	            available.add(v.getNodeId());
	          }
	        }
	      }
	    }

	    return available;
	  }

	  public String toString() {
	    return questVertices.toString();
	  }
}
